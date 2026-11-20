import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from tools.catalogo.buscar_herramientas import (
    check_availability, load_catalog, search_tools, validate_catalog,
)
from tools.casos.registro_casos import CaseStore
from tools.diagnostico_usb.validar_medicion import measurement_readiness, validate_measurement
from tools.flujo_reparacion.workflow import (
    add_device, add_local_notification, add_photo_reference, build_summary,
    create_batch_plan, new_case, save_case,
)


class CatalogTests(unittest.TestCase):
    def test_catalog_and_legacy_expansion_are_valid(self):
        catalog = load_catalog()
        self.assertEqual(validate_catalog(catalog), [])
        self.assertIn("phones", {category["id"] for category in catalog["device_categories"]})

    def test_existing_catalog_capabilities_are_preserved(self):
        ids = {item["id"] for item in load_catalog()["tools"]}
        expected = {
            "android-platform-tools", "usb-inventory", "python-usb-serial",
            "qualcomm-edl-reference", "mediatek-reference", "usb-meter-adapter",
            "user-scripts", "apple-devices-windows", "apple-finder-device-support",
            "apple-recovery-guides", "rufus-bootable-usb", "ventoy-bootable-usb",
            "windows-installation-media", "macos-bootable-installer", "linux-distribution-media",
            "usb-inspector", "usb-microscope", "apple-device-support",
            "mediatek-service-reference", "usb-measurement-adapter",
        }
        self.assertTrue(expected.issubset(ids), expected - ids)

    def test_protocol_search_returns_adb_fastboot(self):
        results = search_tools(load_catalog(), protocol="fastboot")
        self.assertIn("android-platform-tools", {item["id"] for item in results})

    def test_chipset_search_returns_mediatek_reference(self):
        results = search_tools(load_catalog(), chipset="MediaTek")
        self.assertIn("mediatek-reference", {item["id"] for item in results})

    def test_device_search_finds_apple_references(self):
        results = search_tools(load_catalog(), device="iphones", brand="Apple")
        ids = {item["id"] for item in results}
        self.assertIn("apple-device-support", ids)
        self.assertIn("apple-devices-windows", ids)

    def test_available_only_excludes_reference_without_dependencies(self):
        catalog = {"tools": [{"id": "reference", "brands": ["Universal"], "protocols": [], "required_commands": []}]}
        self.assertEqual(search_tools(catalog, available_only=True), [])

    @patch("tools.catalogo.buscar_herramientas.shutil.which", return_value="/usr/bin/adb")
    def test_dependency_availability_detected(self, _which):
        item = {"required_commands": ["adb"], "nix_package": "android-tools"}
        report = check_availability(item)
        self.assertTrue(report["available"])
        self.assertEqual(report["missing"], [])

    @patch("tools.catalogo.buscar_herramientas.shutil.which", side_effect=lambda command: "/usr/bin/adb" if command == "adb" else None)
    def test_missing_one_of_multiple_commands_is_reported(self, _which):
        report = check_availability({"required_commands": ["adb", "fastboot"]})
        self.assertFalse(report["available"])
        self.assertEqual(report["missing"], ["fastboot"])

    def test_merge_preserves_unique_legacy_expansion_entries(self):
        catalog = load_catalog()
        ids = {item["id"] for item in catalog["tools"]}
        self.assertIn("android-oem-update-support", ids)
        self.assertIn("linux-distribution-media", ids)


class MeasurementTests(unittest.TestCase):
    def test_valid_calibrated_samples_are_ready_for_review_not_a_diagnosis(self):
        payload = {"protocol": "kdf-measurement-json-v1", "measurement": {
            "calibration_id": "cal-1", "samples": [
                {"t_ns": 1, "vbus_mv": 5000, "input_current_ma": 100},
                {"t_ns": 2, "vbus_mv": 5000, "input_current_ma": 110}
            ]
        }}
        self.assertEqual(validate_measurement(payload), (True, []))
        result = measurement_readiness(payload)
        self.assertEqual(result["status"], "ready_for_technical_review")
        self.assertIsNone(result["diagnosis"])

    def test_rejects_non_monotonic_samples(self):
        payload = {"protocol": "kdf-measurement-json-v1", "measurement": {"samples": [
            {"t_ns": 10, "vbus_mv": 5}, {"t_ns": 9, "vbus_mv": 5}
        ]}}
        valid, errors = validate_measurement(payload)
        self.assertFalse(valid)
        self.assertTrue(any("no puede retroceder" in error for error in errors))


class CaseStoreTests(unittest.TestCase):
    def test_add_search_update_and_delete_case(self):
        with tempfile.TemporaryDirectory() as directory:
            store = CaseStore(Path(directory) / "cases.jsonl")
            case = store.add({"brand": "Example", "model": "M1", "symptoms": ["no enciende"]})
            self.assertEqual(len(store.search(model="M1", symptom="enciende")), 1)
            self.assertTrue(store.set_outcome(case["case_id"], "confirmed"))
            self.assertEqual(store.search()[0]["outcome"], "confirmed")
            store.delete_all()
            self.assertEqual(store.search(), [])

    def test_rejects_personal_identifiers(self):
        with tempfile.TemporaryDirectory() as directory:
            store = CaseStore(Path(directory) / "cases.jsonl")
            with self.assertRaises(ValueError):
                store.add({"imei": "not-stored"})


class RepairWorkflowTests(unittest.TestCase):
    def test_multi_device_photo_batch_notification_and_report(self):
        case = new_case(category="smartphones", mode="assistant", brand="Example", model="M1")
        first = add_device(case, label="equipo principal", transport="usb_otg", vendor_id="1234", product_id="abcd")
        add_device(case, label="microscopio", transport="uvc")
        attachment = add_photo_reference(case, app_private_relative_path="cases/demo/front.jpg", caption="Frente")
        batch = create_batch_plan(case["devices"], checks=["usb_inventory", "read_only_device_info"])
        add_local_notification(case, event="diagnostic_ready", message="Informe listo")
        self.assertEqual(attachment["storage"], "app_private")
        self.assertFalse(batch["execution_started"])
        case["diagnostic_results"].append({"outcome": "confirmed"})
        self.assertEqual(build_summary([case])["diagnostic_outcomes"]["confirmed"], 1)
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "cases.jsonl"
            save_case(path, case)
            self.assertEqual(len(json.loads(path.read_text(encoding="utf-8"))["devices"]), 2)

    def test_rejects_photo_path_traversal_and_personal_data(self):
        case = new_case(category="other-usb")
        with self.assertRaises(ValueError):
            add_photo_reference(case, app_private_relative_path="../../private.jpg")
        case["customer_name"] = "not allowed"
        with tempfile.TemporaryDirectory() as directory:
            with self.assertRaises(ValueError):
                save_case(Path(directory) / "case.jsonl", case)

    def test_batch_is_plan_only_and_validates_inputs(self):
        with self.assertRaises(ValueError):
            create_batch_plan([], checks=["usb_inventory"])
        case = new_case(category="tablets")
        device = add_device(case, label="tablet", transport="usb")
        plan = create_batch_plan([device], checks=["read_only"])
        self.assertEqual(plan["status"], "planned")
        self.assertEqual(plan["devices"][0]["status"], "pending")


if __name__ == "__main__":
    unittest.main()
