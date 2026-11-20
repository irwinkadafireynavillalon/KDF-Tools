import json
import tempfile
import unittest
from pathlib import Path

from tools.catalogo.buscar_herramientas import load_catalog, search_tools
from tools.flujo_reparacion.workflow import (
    add_device, add_local_notification, add_photo_reference, build_summary,
    create_batch_plan, new_case, save_case,
)


class CatalogExpansionTests(unittest.TestCase):
    def test_expanded_catalog_loads_with_apple_and_usb_microscope(self):
        catalog = load_catalog()
        ids = {item["id"] for item in catalog["tools"]}
        self.assertIn("apple-device-support", ids)
        self.assertIn("usb-microscope", ids)

    def test_search_apple_and_fastboot(self):
        self.assertIn("apple-device-support", {x["id"] for x in search_tools(load_catalog(), brand="Apple")})
        self.assertIn("android-platform-tools", {x["id"] for x in search_tools(load_catalog(), protocol="fastboot")})


class RepairWorkflowTests(unittest.TestCase):
    def test_intake_multi_device_photo_batch_notification_and_report(self):
        case = new_case(category="smartphones", mode="assistant", brand="Example", model="M1")
        first = add_device(case, label="equipo principal", transport="usb_otg", vendor_id="1234", product_id="abcd")
        add_device(case, label="microscopio", transport="uvc")
        photo = add_photo_reference(case, app_private_relative_path="cases/demo/front.jpg", caption="Frente")
        self.assertEqual(photo["storage"], "app_private")
        batch = create_batch_plan(case["devices"], checks=["usb_inventory", "read_only_device_info"])
        self.assertFalse(batch["execution_started"])
        notification = add_local_notification(case, event="diagnostic_ready", message="Informe listo")
        self.assertEqual(notification["channel"], "local")
        case["diagnostic_results"].append({"outcome": "confirmed"})
        report = build_summary([case])
        self.assertEqual(report["case_count"], 1)
        self.assertEqual(report["diagnostic_outcomes"]["confirmed"], 1)
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "cases.jsonl"
            save_case(path, case)
            saved = json.loads(path.read_text(encoding="utf-8"))
            self.assertEqual(len(saved["devices"]), 2)
            self.assertEqual(saved["devices"][0]["device_id"], first["device_id"])

    def test_rejects_photo_path_traversal_and_personal_data(self):
        case = new_case(category="other-usb")
        with self.assertRaises(ValueError):
            add_photo_reference(case, app_private_relative_path="../../private.jpg")
        case["notes"] = ""
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
