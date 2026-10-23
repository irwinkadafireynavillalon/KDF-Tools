package com.kdf.stresslab

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.kdf.stresslab.databinding.ActivityMainBinding
import com.kdf.stresslab.pipeline.ApkInstallerHelper
import com.kdf.stresslab.pipeline.AutomatedStressPipeline
import com.kdf.stresslab.telemetry.FpsJankTracker
import com.kdf.stresslab.telemetry.TelemetryCollector
import org.json.JSONObject
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apkHelper: ApkInstallerHelper
    private lateinit var telemetryCollector: TelemetryCollector
    private val fpsTracker = FpsJankTracker()
    private val mainHandler = Handler(Looper.getMainLooper())

    // Selector de archivos APK o ZIP del sistema Android
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            handleSelectedFile(uri)
        } else {
            Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apkHelper = ApkInstallerHelper(this)
        telemetryCollector = TelemetryCollector(this)

        setupListeners()
        startTelemetryMonitoring()
    }

    private fun setupListeners() {
        binding.btnUploadFile.setOnClickListener {
            // Permite seleccionar archivos APK o ZIP
            filePickerLauncher.launch("*/*")
        }
    }

    private fun handleSelectedFile(uri: Uri) {
        appendLog("📂 Archivo recibido. Extrayendo metadatos y validando integridad...")
        binding.tvFileLoadedStatus.text = "Procesando archivo: ${uri.lastPathSegment}..."

        Thread {
            val metadata = apkHelper.processUploadedUri(uri)
            runOnUiThread {
                if (metadata != null) {
                    binding.tvFileLoadedStatus.text = "🎯 ${metadata.appName} (v${metadata.versionName})\nPaquete: ${metadata.packageName} | TargetSDK: ${metadata.targetSdk}"
                    appendLog("✅ APK extraído con éxito: ${metadata.packageName}")
                    appendLog("🔒 Permisos sensibles detectados: ${metadata.dangerousPermissions.size}")

                    // Disparar instalación si aún no está en el sistema
                    apkHelper.triggerInstallation(metadata.apkFile)

                    // Iniciar el pipeline automatizado extremo
                    startStressPipeline(metadata)
                } else {
                    binding.tvFileLoadedStatus.text = "❌ Error: El archivo no es un APK válido o el ZIP no contiene ningún APK."
                    appendLog("❌ Falló la extracción del APK.")
                    Toast.makeText(this, "Archivo no compatible. Sube un APK o ZIP.", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun startStressPipeline(metadata: ApkInstallerHelper.ExtractedApkMetadata) {
        binding.cardProgress.visibility = View.VISIBLE
        binding.cardReport.visibility = View.GONE
        fpsTracker.start()

        val pipeline = AutomatedStressPipeline(
            context = this,
            onStepUpdate = { stepNumber, title, details ->
                binding.progressBarStress.progress = stepNumber
                binding.tvPipelinePhaseTitle.text = "⚡ FASE $stepNumber / 8: $title"
                binding.tvPipelinePhaseDetails.text = details
                appendLog("[$stepNumber/8] $title: $details")
            },
            onCompleted = { reportJson ->
                fpsTracker.stop()
                displayFinalReport(reportJson)
            }
        )

        pipeline.runFullPipeline(metadata)
    }

    private fun displayFinalReport(reportJson: String) {
        binding.cardProgress.visibility = View.GONE
        binding.cardReport.visibility = View.VISIBLE

        try {
            val json = JSONObject(reportJson)
            val verdict = json.getJSONObject("verdict")
            val score = verdict.getString("overall_resilience_score")
            val readiness = verdict.getString("play_store_readiness")

            binding.tvVerdictScore.text = "PUNTUACIÓN: $score"
            binding.tvVerdictStatus.text = readiness
            binding.tvReportJsonSummary.text = reportJson

            appendLog("🏆 PRUEBA EXTREMA COMPLETADA. Reporte guardado en Download/.")
            Toast.makeText(this, "Diagnóstico finalizado con éxito", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            binding.tvReportJsonSummary.text = reportJson
        }
    }

    private fun startTelemetryMonitoring() {
        mainHandler.post(object : Runnable {
            override fun run() {
                val mem = telemetryCollector.captureMemorySnapshot()
                binding.tvCpuUsage.text = "CPU Stress: Activo | Hilos: ${Thread.activeCount()}"
                binding.tvMemoryUsage.text = "RAM PSS: ${mem.totalPssMb} MB | Libre: ${mem.systemAvailableMb} MB"

                val jankPct = fpsTracker.getJankPercentage()
                binding.tvFpsJank.text = String.format(Locale.US, "Fotogramas: %d | Jank (Lag): %d (%.1f%%)", fpsTracker.totalFrames.get(), fpsTracker.jankFrames.get(), jankPct)

                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun appendLog(msg: String) {
        val current = binding.tvLogs.text.toString()
        val lines = current.split("\n").takeLast(25).joinToString("\n")
        binding.tvLogs.text = "$lines\n$msg"
    }

    override fun onDestroy() {
        fpsTracker.stop()
        super.onDestroy()
    }
}
