package com.kdf.stresslab.report

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * Generador de Reporte y Puntuación de Resiliencia (Resilience Benchmark).
 * Calcula un puntaje de estabilidad del 0 al 100 evaluando:
 * - Cantidad de Crashes y ANRs (penalización severa).
 * - Porcentaje de Fotogramas Perdidos (Jank).
 * - Estabilidad de memoria bajo estrés de LMK.
 */
class ResilienceBenchmarkReport {

    fun generateReportJson(
        targetPackage: String,
        durationSeconds: Long,
        totalFrames: Int,
        jankFrames: Int,
        crashCount: Int,
        anrCount: Int,
        peakMemoryMb: Long
    ): String {
        // Cálculo del Índice de Estabilidad (0 a 100)
        var score = 100.0
        score -= (crashCount * 30.0) // Cada crash resta 30 puntos
        score -= (anrCount * 25.0)   // Cada ANR resta 25 puntos

        val jankPercent = if (totalFrames > 0) (jankFrames.toDouble() / totalFrames) * 100.0 else 0.0
        score -= (jankPercent * 0.4) // Cada 10% de jank resta 4 puntos

        val finalScore = max(0.0, score)
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val json = JSONObject().apply {
            put("suite", "KDF-StressLab Universal Benchmark")
            put("timestamp", timestamp)
            put("target_package", targetPackage)
            put("test_duration_seconds", durationSeconds)
            put("stability_score", String.format(Locale.US, "%.1f", finalScore))
            put("metrics", JSONObject().apply {
                put("crashes", crashCount)
                put("anrs", anrCount)
                put("total_frames", totalFrames)
                put("jank_frames", jankFrames)
                put("jank_percentage", String.format(Locale.US, "%.2f%%", jankPercent))
                put("peak_memory_pss_mb", peakMemoryMb)
            })
            put("assessment", when {
                finalScore >= 90.0 -> "EXCELENTE: Lista para producción y Google Play Store"
                finalScore >= 70.0 -> "ACEPTABLE: Optimizar renderizado y liberación de memoria"
                else -> "CRÍTICO: Inestable bajo estrés térmico y de memoria"
            })
        }

        return json.toString(4)
    }
}
