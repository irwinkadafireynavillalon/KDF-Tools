# ⚡ KDF-TOOLS · UNIVERSAL SUITE (EDICIÓN MEWTWO BIMETÁLICO)

[![Android](https://img.shields.io/badge/Platform-Android%20%7C%20Linux%20Termux-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/irwinkadafireynavillalon/KDF-Tools)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Python](https://img.shields.io/badge/Python-3.11%2B-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org)
[![Gemini](https://img.shields.io/badge/AI-Gemini%202.0%20%7C%20Spark%20Core-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://aistudio.google.com)
[![License](https://img.shields.io/badge/License-MIT-gold?style=for-the-badge)](LICENSE)

> **Arquitectura Bimetálica Autónoma (Oro · Plata · Obsidiana · Púrpura Psíquico)**  
> Suite integral de desarrollo, diagnóstico de hardware a bajo nivel, mantenimiento de firmware y banco de pruebas de fatiga extrema con inteligencia artificial integrada.

---

## 🌌 Visión General del Ecosistema

**KDF-Tools (Arquitectura KDF-LuX)** es una plataforma multi-entorno diseñada para operar directamente desde dispositivos móviles (vía **Termux**) o estaciones de trabajo, combinando herramientas de ingeniería de bajo nivel (ADB, Fastboot, EDL 9008) con un motor dual de inteligencia artificial (**Google AI Studio Gemini + Spark Heuristic Engine**).

```
                      ┌───────────────────────────────────────┐
                      │   ⚡ KDF-TOOLS CORE PLATFORM          │
                      └──────────────────┬────────────────────┘
                                         │
        ┌────────────────────────────────┼────────────────────────────────┐
        │                                │                                │
        ▼                                ▼                                ▼
┌──────────────────┐          ┌──────────────────────┐          ┌──────────────────┐
│ 🛠️ HARDWARE &    │          │ 🤖 DUAL AI ENGINE    │          │ 🌪️ KDF-STRESSLAB │
│    MAINTENANCE   │          │                      │          │    HARNESS       │
├──────────────────┤          ├──────────────────────┤          ├──────────────────┤
│ • ADB / Fastboot │          │ • Gemini Cloud API   │          │ • CPU 100% Load  │
│ • EDL 9008 Flash │          │ • Spark Heuristic v2 │          │ • RAM LMK Stress │
│ • Safe Mode DPC  │          │ • Local Gemma 2B     │          │ • Flash I/O Storm│
│ • Blocker Bypass │          │ • Live Voice Call    │          │ • Chaos Monkey   │
│ • Memory Purge   │          │ • Bio-Telemetry      │          │ • Detailed Report│
└──────────────────┘          └──────────────────────┘          └──────────────────┘
```

---

## 🚀 Componentes Principales

### 1. 🎛️ Panel Web Interactivo (`app.py`)
Servidor web de alta reactividad en el puerto `:5000` con paleta Mewtwo:
* **Diagnóstico de Dispositivos:** Detección en caliente de conexiones ADB, Fastboot y EDL.
* **Control de Módulos:**
  * `enter_safe_mode.sh`: Activación y salida de modo seguro persistente.
  * `disable_blocker.sh`: Desactivación de apps administrativas (MDM, PayJoy, Knox).
  * `clean_memory.sh`: Purga de cachés del sistema y compactación de memoria RAM.
  * `unlock_bootloader.sh` & `edl_flasher.sh`: Automatización de flasheo y particiones A/B.
* **Consola de IA & Asistente:** Consultas técnicas en tiempo real con memoria de contexto del dispositivo.

### 2. 📱 Aplicación Android Nativa (`kdf-tools_extracted/`)
Aplicación moderna desarrollada con **Jetpack Compose**, **Material 3** y arquitectura limpia:
* **Integración Google AI Studio:** Integrada mediante el plugin oficial `secrets-gradle-plugin` para llamadas directas a `gemini-3.5-flash` y generación de arte `gemini-3-pro-image-preview`.
* **Bio-Telemetría:** Monitoreo de sensores de acelerómetro, giroscopio y estado térmico.
* **Soporte USB Host:** Detección de periféricos e interfaces serie.

### 3. 🌪️ KDF-StressLab (`stress_lab/`)
Banco de pruebas de resiliencia automatizado:
* **Entrada Universal:** Admite archivos `.apk` o `.zip`.
* **Fatiga Extrema:**
  * **CPU Stress:** Satura los 8 núcleos al 100% con cálculo en coma flotante.
  * **RAM Pressure:** Asignación de bloques nativos hasta forzar `TRIM_MEMORY_RUNNING_CRITICAL`.
  * **Storage Storm:** Escritura y purga de ráfagas continuas de 25 MB/s.
  * **Network Chaos:** Inyección de fluctuaciones y pérdida artificial de paquetes.
  * **Monkey Fuzzer:** 1,500 eventos táctiles y de rotación aleatorios.
* **Reporte de Estabilidad:** Puntuación de resiliencia (0 a 100), conteo de ANRs, crashes fatales, fotogramas perdidos (*Jank*) y veredicto para Google Play Store.

---

## ⚡ Inicio Rápido (Quick Start)

### En Termux (Android):
```bash
# 1. Clonar el repositorio
git clone https://github.com/irwinkadafireynavillalon/KDF-Tools.git
cd KDF-Tools

# 2. Configurar tus credenciales de Google AI Studio
cp .env.example .env
# Edita .env y agrega tu GEMINI_API_KEY

# 3. Lanzar la suite interactiva
python3 app.py
```

### Probar un APK con KDF-StressLab:
```bash
# Ejecutar prueba de fatiga sobre cualquier APK o ZIP:
python3 stress_lab/harness_cli/kdf_stress_runner.py mi_aplicacion.apk
```

---

## 🔒 Seguridad y Privacidad

* **Cero Filtración de Credenciales:** Todas las claves (`GEMINI_API_KEY`, keystores, tokens) se aíslan estrictamente en `.env` y están protegidas por `.gitignore`.
* **Red Blindada:** Los servicios internos (`:5000`, `:4250`) operan con firmas enmascaradas (`KDF-Shield/2.0`) evitando la exposición pública accidental.

---

## 👤 Autor & Créditos

* **Desarrollador Principal:** [Irwin Kadafi Reyna Villalón (AI LuX Script)](https://github.com/irwinkadafireynavillalon)
* **Arquitectura:** KDF-Tools / Mewtwo Bimetallic Edition
* **Comunidad:** Google AI Studio, Termux Open Source Community
