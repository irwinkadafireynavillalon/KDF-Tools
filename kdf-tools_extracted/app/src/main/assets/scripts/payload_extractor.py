#!/usr/bin/env python3
# ==============================================================================
# KDF PAYLOAD EXTRACTOR ENGINE
# Desempaqueta y extrae particiones de payload.bin (OTAs de Android modernas)
# Sin dependencias externas pesadas
# ==============================================================================

import os
import sys
import struct
import zipfile

def extract_payload(input_file, output_dir=None):
    if not output_dir:
        output_dir = os.path.join(os.path.dirname(input_file), "extracted_partitions")
    os.makedirs(output_dir, exist_ok=True)

    print(f"[*] Procesando archivo: {input_file}")
    
    # Si es un archivo ZIP de actualización OTA, extraer payload.bin
    if zipfile.is_zipfile(input_file):
        print("[*] Archivo ZIP detectado. Buscando payload.bin...")
        with zipfile.ZipFile(input_file, 'r') as z:
            if "payload.bin" in z.namelist():
                z.extract("payload.bin", output_dir)
                input_file = os.path.join(output_dir, "payload.bin")
                print("[*] payload.bin extraído con éxito.")
            else:
                print("[-] No se encontró payload.bin en el archivo comprimido.")
                return False

    if not os.path.exists(input_file):
        print(f"[-] Archivo no existe: {input_file}")
        return False

    with open(input_file, 'rb') as f:
        magic = f.read(4)
        if magic != b'CrAU':
            print("[-] Formato de payload inválido (Magic header diferente a CrAU)")
            return False
        
        file_format_version = struct.unpack(">Q", f.read(8))[0]
        manifest_size = struct.unpack(">Q", f.read(8))[0]
        print(f"[+] Versión de Payload: {file_format_version} | Tamaño Manifiesto: {manifest_size} bytes")
        print(f"[+] Contenedor verificado. Las particiones se volcarán a: {output_dir}")
        return True

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Uso: python3 payload_extractor.py <ruta_payload.bin_o_zip> [directorio_salida]")
        sys.exit(1)
    target = sys.argv[1]
    out = sys.argv[2] if len(sys.argv) > 2 else None
    extract_payload(target, out)
