#!/usr/bin/env python3
# ==============================================================================
# KDF-CLIENT SDK · Conector Automático de IA para Todas las Aplicaciones KDF
#
# Las aplicaciones NUNCA conocen ni ven "localhost" ni "127.0.0.1".
# Toda la comunicación se realiza exclusivamente a través del ATAJO CUBIERTO:
#   http://KDF-STUDIO
# ==============================================================================

import os
import json
import socket
import http.client
import urllib.request
import urllib.error

ENV_FILE = os.path.expanduser("~/.kdf-studio/.env")
STATE_FILE = os.path.expanduser("~/.kdf-studio/kdf_state.json")

def _read_covered_alias():
    alias = "KDF-STUDIO"
    if os.path.exists(ENV_FILE):
        try:
            with open(ENV_FILE, "r") as f:
                for line in f:
                    if line.startswith("KDF_STUDIO_ALIAS="):
                        alias = line.strip().split("=", 1)[1].strip()
                        break
        except Exception:
            pass
    return alias

def _read_internal_host():
    host = "127.0.0.1"
    if os.path.exists(ENV_FILE):
        try:
            with open(ENV_FILE, "r") as f:
                for line in f:
                    if line.startswith("INTERNAL_RAW_HOST="):
                        host = line.strip().split("=", 1)[1].strip()
                        break
        except Exception:
            pass
    return host

class _KDFConnection(http.client.HTTPConnection):
    def connect(self):
        # El socket físico de bajo nivel se enlaza de forma privada
        raw_host = _read_internal_host()
        self.sock = socket.create_connection((raw_host, self.port), self.timeout)

class _KDFHandler(urllib.request.HTTPHandler):
    def http_open(self, req):
        return self.do_open(_KDFConnection, req)

_kdf_opener = urllib.request.build_opener(_KDFHandler)

class KDFClient:
    def __init__(self, retries=2):
        self.retries = retries
        self.state = None
        self.covered_alias = _read_covered_alias()
        self._load_state()

    def _load_state(self):
        if not os.path.exists(STATE_FILE):
            raise RuntimeError("KDF Shield no está inicializado.")
        with open(STATE_FILE, "r") as f:
            self.state = json.load(f)

    def is_alive(self):
        try:
            self._load_state()
            port = self.state["port"]
            token = self.state["token"]

            # Toda llamada viaja identificándose exclusivamente con el atajo cubierto
            req = urllib.request.Request(
                f"http://{self.covered_alias}:{port}/status",
                headers={
                    "Host": f"{self.covered_alias}:{port}",
                    "X-KDF-Key": token,
                    "User-Agent": "KDF-Shield-CoveredClient/2.0"
                }
            )
            with _kdf_opener.open(req, timeout=3) as resp:
                return resp.status == 200
        except Exception:
            return False

    def ask(self, prompt, system_prompt=None, temperature=0.45, max_tokens=1024):
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})
        return self.chat(messages, temperature=temperature, max_tokens=max_tokens)

    def chat(self, messages, temperature=0.45, max_tokens=1024):
        for attempt in range(self.retries + 1):
            try:
                self._load_state()
                port = self.state["port"]
                token = self.state["token"]

                payload = json.dumps({
                    "messages": messages,
                    "temperature": temperature,
                    "max_tokens": max_tokens
                }).encode("utf-8")

                req = urllib.request.Request(
                    f"http://{self.covered_alias}:{port}/chat",
                    data=payload,
                    headers={
                        "Host": f"{self.covered_alias}:{port}",
                        "X-KDF-Key": token,
                        "Content-Type": "application/json",
                        "User-Agent": "KDF-Shield-CoveredClient/2.0"
                    }
                )

                with _kdf_opener.open(req, timeout=120) as resp:
                    data = json.loads(resp.read().decode("utf-8"))
                    return data.get("response", "")

            except Exception as e:
                if attempt < self.retries:
                    import time
                    time.sleep(1)
                    continue
                return f"[Error KDF AI]: Atajo {self.covered_alias} no respondió ({str(e)})"

if __name__ == "__main__":
    client = KDFClient()
    print(f"[*] Atajo Activo: http://{client.covered_alias}")
    print("[*] Estado de Conexión Cubierta:", "VÁLIDA" if client.is_alive() else "OFFLINE")
