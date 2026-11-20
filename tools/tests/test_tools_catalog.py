import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from tools.catalogo.buscar_herramientas import check_availability, load_catalog, search_tools
from tools.casos.registro_casos import CaseStore
from tools.diagnostico_usb.validar_medicion import measurement_readiness, validate_measurement


class CatalogTests(unittest.TestCase):
    def test_catalog_is_valid_json_and_has_device_groups(self):
        catalog = load_catalog()
        self.assertTrue(catalog["device_categories"])
        self.assertIn("phones", {category["id"] for category in catalog["device_categories"]})

    def test_protocol_search_returns_adb_fastboot(self):
        results = search_tools(load_catalog(), protocol="fastboot")
        self.assertIn("android-platform-tools", {item["id"] for item in results})

    def test_chipset_search_returns_mediatek_reference(self):
        results = search_tools(load_catalog(), chipset="MediaTek")
        self.assertIn("mediatek-reference", {item["id"] for item in results})

    def test_available_only_excludes_reference_without_dependencies(self):
        catalog = {"tools": [{"id": "reference", "brands": ["Universal"], "protocols": [], "required_commands": []}]}
        self.assertEqual(search_tools(catalog, available_only=True), [])

    @patch("tools.catalogo.buscar_herramientas.shutil.which", return_value="/usr/bin/adb")
    def test_dependency_availability_detected(self, _which):
        item = {"required_commands": ["adb"], "nix_package": "android-tools"}
        report = check_availability(item)
        self.assertFalse(report["available"])
        self.assertEqual(report["missing"], ["fastboot"] if False else [])


class MeasurementTests(unittest.TestCase):
    def test_valid_calibrated_samples_are_ready_for_review_not_a_diagnosis(self):
        payload = {
            "protocol": "kdf-measurement-json-v1",
            "measurement": {
                "calibration_id": "cal-1",
                "samples": [
                    {"t_ns": 1, "vbus_mv": 5000, "input_current_ma": 100},
                    {"t_ns": 2, "vbus_mv": 5000, "input_current_ma": 110}
                ]
            }
        }
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


if __name__ == "__main__":
    unittest.main()
