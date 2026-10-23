package com.kdf.stresslab.chaos

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Motor de Saturación de E/S de Almacenamiento (Flash Storage Stress).
 * Escribe y borra ráfagas continuas de archivos temporales de 25-50 MB
 * para saturar las colas de I/O de la memoria flash e inducir retrasos
 * en operaciones de base de datos SQLite y lectura de cachés.
 */
class StorageStressEngine(private val context: Context) {

    private val TAG = "StorageStressEngine"
    private val isRunning = AtomicBoolean(false)
    private var ioJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun startIoStress(fileSizeMb: Int = 25, iterations: Int = 50) {
        if (isRunning.getAndSet(true)) return

        val stressDir = File(context.cacheDir, "stress_io_temp").apply { mkdirs() }
        val dummyData = ByteArray(1024 * 1024) { 0x55.toByte() } // 1MB buffer

        ioJob = scope.launch {
            var round = 0
            while (isActive && isRunning.get() && round < iterations) {
                round++
                val tempFile = File(stressDir, "stress_chunk_$round.bin")
                try {
                    FileOutputStream(tempFile).use { fos ->
                        for (i in 0 until fileSizeMb) {
                            if (!isRunning.get()) break
                            fos.write(dummyData)
                        }
                        fos.flush()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Excepción durante estrés de I/O: ${e.message}")
                } finally {
                    if (tempFile.exists()) tempFile.delete()
                }
                delay(100)
            }
            isRunning.set(false)
        }
    }

    fun stop() {
        isRunning.set(false)
        ioJob?.cancel()
        ioJob = null
        File(context.cacheDir, "stress_io_temp").deleteRecursively()
    }

    fun isActive(): Boolean = isRunning.get()
}
