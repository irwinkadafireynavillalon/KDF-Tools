#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-LuX - Hydra EDL Engine (Qualcomm Firehose & EDL 9008)
Módulo independiente para inspección de puertos Qualcomm HS-USB QDLoader 9008,
validación de loaders Firehose (MBN/ELF) y parseo de tablas rawprogram / patch XML.
"""

import os
import sys
import glob
import xml.etree.ElementTree as ET

def find_qualcomm_ports():
    """Detecta puertos serie o dispositivos USB asociados a Qualcomm 9008."""
    ports = []
    # 1. Comprobar /dev/ttyUSB* y /dev/ttyACM*
    for dev in glob.glob("/dev/ttyUSB*") + glob.glob("/dev/ttyACM*") + glob.glob("/dev/qcserial*"):
        ports.append(dev)
    return ports

def parse_rawprogram_xml(filepath):
    """Parsea archivos rawprogram0.xml para verificar la lista de particiones."""
    if not os.path.exists(filepath):
        return {"error": f"Archivo no encontrado: {filepath}"}
    try:
        tree = ET.parse(filepath)
        root = tree.getroot()
        partitions = []
        for program in root.findall("program"):
            label = program.get("label", "")
            filename = program.get("filename", "")
            size_kb = program.get("size_in_KB", "0")
            start_sector = program.get("start_sector", "0")
            partitions.append({
                "label": label,
                "file": filename,
                "size_kb": size_kb,
                "sector": start_sector
            })
        return {"status": "ok", "total_partitions": len(partitions), "partitions": partitions}
    except Exception as e:
        return {"status": "error", "message": str(e)}

if __name__ == "__main__":
    print("=== Hydra EDL Engine (Qualcomm 9008 Service) ===")
    ports = find_qualcomm_ports()
    print(f"[*] Puertos serie/EDL detectados: {ports or 'Ninguno conectado actualmente'}")
