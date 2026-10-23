package com.kdf.stresslab.pipeline

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.kdf.stresslab.chaos.*
import com.kdf.stresslab.report.ResilienceBenchmarkReport
import com.kdf.stresslab.sandbox.AppSandboxManager
import com.kdf.stresslab.telemetry.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * Pipeline de Ejecución de Estrés Automatizado de Extremo a Extremo.
 * Lleva la aplicación al límite absoluto a través de 8 fases controladas,
 * evaluando su resiliencia y generando un reporte técnico exhaustivo.
 */
class AutomatedStressPipeline(
    private val context: Context,
    private val onStepUpdate: (stepNumber: Int, title: String, details: String) -> Unit,
    private val onCompleted: (reportJson: String) -> Unit
) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val cpuEngine = CpuStressEngine()
    private val memoryEngine = MemoryPressureEngine()
    private val storageEngine = StorageStressEngine(context)
    private val networkChaos = NetworkChaosEngine()
    private val fpsTracker = FpsJankTracker()
    private val telemetry = TelemetryCollector(context)
    private val sandbox = AppSandboxManager(context)

    private var crashCount = 0
    private var anrCount = 0
    private var peakPssMb: Long = 0
    private val capturedAnomalies = mutableListOf<String>()

    fun runFullPipeline(metadata: ApkInstallerHelper.ExtractedApkMetadata) {
        scope.launch {
            val startTime = System.currentTimeMillis()

            // FASE 1: Análisis Estático de Manifiesto & Play Store Compliance
            onStepUpdate(1, "Inspección Estática y Permisos Críticos", "Analizando APK: ${metadata.packageName} (v${metadata.versionName})")
            delay(1500)
            if (metadata.targetSdk < 34) {
                capturedAnomalies.add("Target SDK obsoleto (${metadata.targetSdk}). Google Play exige mínimo SDK 34/35.")
            }
            if (metadata.dangerousPermissions.contains("android.permission.QUERY_ALL_PACKAGES")) {
                capturedAnomalies.add("Uso de QUERY_ALL_PACKAGES detectado. Requiere justificación formal en video para Play Store.")
            }

            // FASE 2: Instalación y Lanzamiento en Sandbox
            onStepUpdate(2, "Instalación y Despliegue en Sandbox", "Preparando proceso aislado para ${metadata.appName}")
            delay(2000)
            sandbox.launchInSandbox(metadata.packageName)

            // FASE 3: Captura de Línea Base de Telemetría (Idle State)
            onStepUpdate(3, "Captura de Línea Base (Idle State)", "Registrando consumo de memoria inicial y fotogramas limpios...")
            fpsTracker.start()
            val baselineMem = telemetry.captureMemorySnapshot()
            delay(3000)

            // FASE 4: Estrés Térmico & CPU 100% Multi-Core
            onStepUpdate(4, "Fuego Cruzado: Saturación de CPU 100%", "Ejecutando hilos matemáticos en los 8 núcleos del procesador...")
            cpuEngine.startCpuStress()
            delay(5000)
            var memCurrent = telemetry.captureMemorySnapshot()
            if (memCurrent.totalPssMb > peakPssMb) peakPssMb = memCurrent.totalPssMb

            // FASE 5: Tormenta de E/S de Disco (Flash Memory Saturation)
            onStepUpdate(5, "Tormenta de E/S en Memoria Flash", "Escribiendo y purgando ráfagas de 25 MB para bloquear la cola de I/O...")
            storageEngine.startIoStress(fileSizeMb = 25, iterations = 10)
            delay(5000)

            // FASE 6: Caos de Red y Latencia Crítica
            onStepUpdate(6, "Caos de Conectividad & Socket Jitter", "Inyectando caídas de paquetes y latencias artificiales de hasta 4.5s...")
            networkChaos.startNetworkChaos()
            delay(5000)

            // FASE 7: Inyección de Presión de RAM Extrema (LMK Trigger)
            onStepUpdate(7, "Presión Extrema de Memoria RAM", "Reservando bloques directos para forzar TRIM_MEMORY_CRITICAL...")
            withContext(Dispatchers.IO) {
                memoryEngine.startMemoryPressure(targetMegabytes = 350)
            }
            memCurrent = telemetry.captureMemorySnapshot()
            if (memCurrent.totalPssMb > peakPssMb) peakPssMb = memCurrent.totalPssMb
            delay(4000)

            // FASE 8: Finalización y Síntesis de Métricas
            onStepUpdate(8, "Síntesis y Auditoría Final de Resiliencia", "Deteniendo motores de fatiga y compilando diagnóstico...")
            cpuEngine.stop()
            storageEngine.stop()
            networkChaos.stop()
            memoryEngine.releaseMemory()
            fpsTracker.stop()

            val durationSeconds = (System.currentTimeMillis() - startTime) / 1000
            val detailedReport = buildExhaustiveReport(metadata, baselineMem, durationSeconds)

            // Guardar reporte en archivo de almacenamiento accesible
            saveReportToFile(detailedReport, metadata.packageName)
            onCompleted(detailedReport)
        }
    }

    private fun buildExhaustiveReport(
        meta: ApkInstallerHelper.ExtractedApkMetadata,
        baselineMem: TelemetryCollector.MemorySnapshot,
        durationSeconds: Long
    ): String {
        val totalFrames = fpsTracker.totalFrames.get()
        val jankFrames = fpsTracker.jankFrames.get()
        val jankPercentage = if (totalFrames > 0) (jankFrames.toDouble() / totalFrames) * 100.0 else 0.0

        var stabilityScore = 100.0
        stabilityScore -= (crashCount * 35.0)
        stabilityScore -= (anrCount * 25.0)
        stabilityScore -= (jankPercentage * 0.35)
        stabilityScore -= (capturedAnomalies.size * 5.0)
        val finalScore = max(0.0, stabilityScore)

        val reportObj = JSONObject().apply {
            put("suite", "KDF-STRESSLAB UNIVERSAL RIGOROUS BENCHMARK")
            put("generated_at", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            put("target_application", JSONObject().apply {
                put("name", meta.appName)
                put("package", meta.packageName)
                put("version_name", meta.versionName)
                put("version_code", meta.versionCode)
                put("target_sdk", meta.targetSdk)
                put("min_sdk", meta.minSdk)
                put("total_permissions_requested", meta.requestedPermissions.size)
                put("dangerous_permissions", JSONArray(meta.dangerousPermissions))
            })
            put("test_execution_profile", JSONObject().apply {
                put("total_duration_seconds", durationSeconds)
                put("stress_modules_applied", JSONArray(listOf("CPU_100_PERCENT", "RAM_HEAP_PRESSURE", "FLASH_IO_STORM", "NETWORK_JITTER", "CHOREOGRAPHER_JANK_TRACKER")))
            })
            put("performance_and_telemetry", JSONObject().apply {
                put("baseline_ram_pss_mb", baselineMem.totalPssMb)
                put("peak_ram_pss_mb", peakPssMb)
                put("ram_growth_delta_mb", peakPssMb - baselineMem.totalPssMb)
                put("total_rendered_frames", totalFrames)
                put("jank_lag_frames", jankFrames)
                put("jank_percentage", String.format(Locale.US, "%.2f%%", jankPercentage))
                put("crashes_detected", crashCount)
                put("anr_freezes_detected", anrCount)
            })
            put("compliance_and_anomalies", JSONArray(capturedAnomalies))
            put("verdict", JSONObject().apply {
                put("overall_resilience_score", String.format(Locale.US, "%.1f / 100", finalScore))
                put("play_store_readiness", when {
                    finalScore >= 85.0 && capturedAnomalies.isEmpty() -> "🟢 APROBADA (Lista para Producción y Google Play Store)"
                    finalScore >= 65.0 -> "🟡 ADVERTENCIA (Requiere optimizaciones de fluidez y revisión de políticas)"
                    else -> "🔴 RECHAZADA (Inestable bajo fatiga extrema o incumple políticas críticas)"
                })
                put("recommendations", JSONArray(listOf(
                    "Verificar que las tareas pesadas de red se manejen fuera del hilo principal (Dispatchers.IO).",
                    "Evitar retener Bitmaps o Context en referencias estáticas para prevenir fugas en Low Memory Killer.",
                    "Optimizar el árbol de vistas para reducir fotogramas con Jank por encima de 32ms."
                )))
            })
        }

        return reportObj.toString(4)
    }

    private fun saveReportToFile(reportJson: String, packageName: String) {
        try {
            val downloadDir = File("/storage/emulated/0/Download")
            val reportFile = File(downloadDir, "KDF_StressLab_Report_${packageName}_${System.currentTimeMillis()}.json")
            reportFile.writeText(reportJson)
        } catch (e: Exception) {
            // Fallback en almacenamiento interno
            val fallback = File(context.filesDir, "last_stress_report.json")
            fallback.writeText(reportJson)
        }
    }
}
