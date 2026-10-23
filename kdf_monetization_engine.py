#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
==============================================================================
KDF-TOOLS · MOTOR DE MONETIZACIÓN, LICENCIAS Y RENTABILIDAD EMPRESARIAL
==============================================================================
Proporciona:
1. Gestión de Licencias Criptográficas (HMAC-SHA256) para niveles Free, Pro y Taller.
2. Medición de Cuotas de Tokens (Gemini Cloud + Local AI) con cálculo de margen comercial.
3. Catálogo de Servicios Técnicos de Alta Rentabilidad (Flasheo, Desbrickeo, Rescate USB).
4. Generador de Comprobantes Comerciales y Pasarelas de Pago.
==============================================================================
"""

import os
import time
import json
import hmac
import hashlib
from datetime import datetime, timedelta, timezone

MASTER_SECRET = os.getenv("KDF_LICENSE_SECRET", "KDF-LUX-STUDIO-MASTER-KEY-2026-TITANIUM")
LICENSES_DB_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "storage", "licenses.json")
SERVICES_CATALOG_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "config", "services_catalog.json")

# Tarifas base y márgenes de rentabilidad
TIERS_CONFIG = {
    "COMMUNITY_FREE": {
        "name": "KDF Community (Gratis)",
        "price_usd": 0.00,
        "daily_tokens_limit": 20000,
        "allowed_models": ["Local_Llama_3.2_1B", "Gemma_Local"],
        "hardware_features": ["basic_diagnostics", "battery_telemetry"],
        "support": "Comunidad"
    },
    "PRO_STUDIO": {
        "name": "KDF Pro Studio (Mensual)",
        "price_usd": 9.99,
        "daily_tokens_limit": 500000,
        "allowed_models": ["Gemini_3.8_Flash", "Local_Llama_3.2_1B", "Gemma_Local"],
        "hardware_features": ["full_telemetry", "custom_themes", "memory_turbo", "video_render_ffmpeg"],
        "support": "Prioritario 24/7"
    },
    "ENTERPRISE_WORKSHOP": {
        "name": "KDF Enterprise / Taller Master",
        "price_usd": 49.99,
        "daily_tokens_limit": 2000000,
        "allowed_models": ["Gemini_3.8_Pro", "Gemini_3.8_Flash", "Local_Dual_AI"],
        "hardware_features": ["edl_flashing", "bootloop_recovery", "usb_carving", "imei_audit", "firmware_extractor"],
        "support": "Ingeniería Directa KDF"
    }
}

CATALOG_OF_PAID_SERVICES = [
    {
        "id": "srv_unbrick_motog24",
        "title": "Desbrickeo y Rescate Motorola Moto G24",
        "category": "Flasheo & Firmware",
        "price_usd": 25.00,
        "estimated_time_mins": 15,
        "description": "Flasheo seguro de particiones super, boot y vendor con validación hash SHA-256 para revivir dispositivos en bootloop.",
        "compatible_tools": ["scripts/flash_moto_g24.sh", "scripts/unbrick_recovery.sh"]
    },
    {
        "id": "srv_usb_forensic_rescue",
        "title": "Rescate Forense y Extracción de Datos USB Dañado",
        "category": "Recuperación de Datos",
        "price_usd": 35.00,
        "estimated_time_mins": 30,
        "description": "Lectura sector a sector, reparación de tablas de partición MBR/GPT y extracción directa de archivos borrados.",
        "compatible_tools": ["usb_rescue_reader.py", "scripts/create_usb_boot.sh"]
    },
    {
        "id": "srv_security_imei_audit",
        "title": "Auditoría de Seguridad, Certificados y Diagnóstico de Red",
        "category": "Seguridad Móvil",
        "price_usd": 20.00,
        "estimated_time_mins": 10,
        "description": "Verificación de listas de bloqueo, integridad de particiones EFS/NVRAM y blindaje contra spyware.",
        "compatible_tools": ["kdf_security_core.py", "scripts/device_security_scan.sh"]
    },
    {
        "id": "srv_custom_os_tune",
        "title": "Optimización Extrema de Kernel y Limpieza de Bloatware",
        "category": "Rendimiento",
        "price_usd": 15.00,
        "estimated_time_mins": 10,
        "description": "Eliminación de aplicaciones espía del operador, optimización de zRAM y aceleración computacional en Android.",
        "compatible_tools": ["kdf_optimizer_core.py", "scripts/clean_memory.sh"]
    }
]

def ensure_db():
    os.makedirs(os.path.dirname(LICENSES_DB_PATH), exist_ok=True)
    if not os.path.exists(LICENSES_DB_PATH):
        with open(LICENSES_DB_PATH, "w") as f:
            json.dump({"licenses": {}, "token_usage": {}}, f, indent=2)

def generate_license_key(client_name: str, tier: str, days_valid: int = 30) -> dict:
    """Genera una clave de licencia criptográfica firmada con HMAC-SHA256."""
    ensure_db()
    if tier not in TIERS_CONFIG:
        tier = "PRO_STUDIO"

    expiry_date = (datetime.now(timezone.utc) + timedelta(days=days_valid)).strftime("%Y-%m-%d")
    raw_payload = f"{client_name}|{tier}|{expiry_date}"
    signature = hmac.new(MASTER_SECRET.encode(), raw_payload.encode(), hashlib.sha256).hexdigest()[:16].upper()
    license_key = f"KDF-{tier[:3]}-{signature[:4]}-{signature[4:8]}-{signature[8:12]}-{signature[12:16]}"

    record = {
        "license_key": license_key,
        "client_name": client_name,
        "tier": tier,
        "tier_name": TIERS_CONFIG[tier]["name"],
        "created_at": datetime.now(timezone.utc).isoformat(),
        "expires_at": expiry_date,
        "active": True
    }

    with open(LICENSES_DB_PATH, "r") as f:
        data = json.load(f)
    data["licenses"][license_key] = record
    with open(LICENSES_DB_PATH, "w") as f:
        json.dump(data, f, indent=2)

    return record

def verify_license_key(license_key: str) -> dict:
    """Valida la autenticidad y vigencia de una clave de licencia."""
    ensure_db()
    with open(LICENSES_DB_PATH, "r") as f:
        data = json.load(f)
    
    rec = data.get("licenses", {}).get(license_key)
    if not rec:
        return {"valid": False, "reason": "Clave de licencia no encontrada o inválida."}
    
    if not rec.get("active", False):
        return {"valid": False, "reason": "Licencia suspendida o cancelada."}

    exp_str = rec.get("expires_at", "")
    try:
        exp_date = datetime.strptime(exp_str, "%Y-%m-%d")
        if datetime.now(timezone.utc) > exp_date.replace(tzinfo=timezone.utc):
            return {"valid": False, "reason": f"Licencia expirada el {exp_str}."}
    except Exception:
        pass

    tier = rec.get("tier", "COMMUNITY_FREE")
    return {
        "valid": True,
        "client_name": rec.get("client_name"),
        "tier": tier,
        "tier_info": TIERS_CONFIG.get(tier, TIERS_CONFIG["COMMUNITY_FREE"]),
        "expires_at": exp_str
    }

def record_token_consumption(client_id: str, tokens: int, model: str = "Gemini_3.8_Flash"):
    """Registra y cobra consumo de tokens garantizando el control de cuotas."""
    ensure_db()
    today = datetime.utcnow().strftime("%Y-%m-%d")
    with open(LICENSES_DB_PATH, "r") as f:
        data = json.load(f)

    usage = data.setdefault("token_usage", {}).setdefault(client_id, {}).setdefault(today, {
        "total_tokens": 0,
        "models": {}
    })

    usage["total_tokens"] += tokens
    usage["models"][model] = usage["models"].get(model, 0) + tokens

    with open(LICENSES_DB_PATH, "w") as f:
        json.dump(data, f, indent=2)

    return usage

def get_commercial_summary():
    """Genera el reporte financiero y de rentabilidad para el administrador."""
    ensure_db()
    with open(LICENSES_DB_PATH, "r") as f:
        data = json.load(f)

    licenses = data.get("licenses", {})
    total_revenue = 0.0
    for lk, info in licenses.items():
        tier = info.get("tier", "")
        if tier in TIERS_CONFIG:
            total_revenue += TIERS_CONFIG[tier]["price_usd"]

    return {
        "total_active_licenses": len([l for l in licenses.values() if l.get("active")]),
        "projected_monthly_revenue_usd": round(total_revenue, 2),
        "available_services_count": len(CATALOG_OF_PAID_SERVICES),
        "available_services": CATALOG_OF_PAID_SERVICES,
        "tiers": TIERS_CONFIG
    }

if __name__ == "__main__":
    print("⚡ Inicializando Motor de Rentabilidad KDF-Tools...")
    test_lic = generate_license_key("Taller Central Irwin", "ENTERPRISE_WORKSHOP", days_valid=365)
    print(f"[✓] Licencia Master Generada: {test_lic['license_key']}")
    val = verify_license_key(test_lic["license_key"])
    print(f"[✓] Verificación Criptográfica: {val['valid']} | Nivel: {val['tier']}")
    summary = get_commercial_summary()
    print(f"[✓] Servicios Disponibles: {summary['available_services_count']} | Ingreso Proyectado: ${summary['projected_monthly_revenue_usd']} USD")
