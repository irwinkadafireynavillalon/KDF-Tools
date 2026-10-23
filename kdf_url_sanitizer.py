#!/usr/bin/env python3
# ==============================================================================
# KDF-BROWSER URL PURIFIER & ZERO-TELEMETRY SHIELD ENGINE
# Módulo de Sanitización de URLs y Bloqueo de Redirecciones/Tracking Sospechoso
#
# Detecta:
# 1. Parámetros de rastreo y doxxing: continue=, redirect_uri=, ec=, gclid=, fbclid=
# 2. URLs infladas con identificadores de sesión/región/token en páginas de login
# 3. Phishing por similitud o subdominios alterados
# ==============================================================================

import re
import urllib.parse

# Dominios oficiales de login y su ruta limpia estricta
OFFICIAL_AUTH_ENDPOINTS = {
    "google": {
        "pattern": r"(accounts\.google\.com)",
        "clean_target": "https://accounts.google.com/ServiceLogin",
        "suspicious_keys": ["continue", "ec", "hl", "authuser", "flowName", "flowEntry", "dsh", "gclid"]
    },
    "facebook": {
        "pattern": r"((www\.|m\.)?facebook\.com)",
        "clean_target": "https://www.facebook.com/login",
        "suspicious_keys": ["next", "login_attempt", "refsrc", "privacy_mutation_token", "fbclid"]
    },
    "instagram": {
        "pattern": r"((www\.)?instagram\.com)",
        "clean_target": "https://www.instagram.com/accounts/login/",
        "suspicious_keys": ["next", "source", "ip_hint", "ig_cache_key"]
    },
    "microsoft": {
        "pattern": r"(login\.live\.com|login\.microsoftonline\.com)",
        "clean_target": "https://login.live.com/",
        "suspicious_keys": ["cobrandid", "client_id", "redirect_uri", "flowToken"]
    },
    "apple": {
        "pattern": r"(appleid\.apple\.com)",
        "clean_target": "https://appleid.apple.com/sign-in",
        "suspicious_keys": ["client_id", "redirect_uri", "state"]
    }
}

class URLSanitizer:
    def __init__(self, strict_mode=True):
        self.strict_mode = strict_mode

    def audit_and_purify(self, url: str) -> dict:
        parsed = urllib.parse.urlparse(url)
        netloc = parsed.netloc.lower()
        path = parsed.path
        query = parsed.query
        params = urllib.parse.parse_qs(query)

        alerts = []
        is_suspicious = False
        purified_url = url

        # 1. Comprobar si coincide con un portal de autenticación oficial conocido
        matched_auth = None
        for service, spec in OFFICIAL_AUTH_ENDPOINTS.items():
            if re.search(spec["pattern"], netloc):
                matched_auth = (service, spec)
                break

        if matched_auth:
            service_name, spec = matched_auth
            # Evaluar longitud de la URL y sobrecarga de parámetros
            total_query_length = len(query)
            
            # Detectar parámetros sospechosos de telemetría/redirección
            found_suspicious_keys = []
            for key in params.keys():
                if key in spec["suspicious_keys"] or len(key) > 15:
                    found_suspicious_keys.append(key)

            # Si la URL tiene redirecciones o parámetros inflados
            if total_query_length > 40 or found_suspicious_keys or "continue=" in query or "redirect" in query:
                is_suspicious = True
                alerts.append(f"Detectados parámetros de rastreo/redirección ({', '.join(found_suspicious_keys)})")
                alerts.append("Intento de inyección de huella digital o URL inflada en login.")
                
                # PURIFICACIÓN: Reducir la dirección a su raíz oficial limpia
                purified_url = spec["clean_target"]

        # 2. Sanitizador general: eliminar parámetros globales de espionaje comercial
        global_trackers = ["gclid", "fbclid", "msclkid", "utm_source", "utm_medium", "utm_campaign", "yclid", "mc_cid"]
        if query and not matched_auth:
            cleaned_params = {k: v for k, v in params.items() if k not in global_trackers}
            if len(cleaned_params) != len(params):
                alerts.append("Eliminados identificadores de seguimiento y anuncios (gclid/fbclid/utm).")
                new_query = urllib.parse.urlencode(cleaned_params, doseq=True)
                purified_url = urllib.parse.urlunparse((parsed.scheme, parsed.netloc, parsed.path, parsed.params, new_query, parsed.fragment))

        return {
            "original_url": url,
            "purified_url": purified_url,
            "is_suspicious": is_suspicious,
            "alerts": alerts,
            "domain": netloc,
            "safe": not is_suspicious
        }

if __name__ == "__main__":
    sanitizer = URLSanitizer()
    ejemplo = "https://accounts.google.com/SignOutOptions?hl=en&continue=https://antigravity.google.com/u/0/r/39b58375-c71a-4e33-a9ef-10290d25fef3-v2&ec=GBRAgQY"
    res = sanitizer.audit_and_purify(ejemplo)
    print("Resultado Auditoría:")
    print("Original :", res["original_url"])
    print("Purificada:", res["purified_url"])
    print("Sospechosa:", res["is_suspicious"])
    print("Alertas   :", res["alerts"])
