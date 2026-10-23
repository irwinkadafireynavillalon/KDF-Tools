package com.kdf.stresslab.chaos

import android.util.Log
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Motor de Presión de Memoria RAM & LMK Trigger.
 * Reserva progresivamente bloques de memoria nativa y heap de 10 MB
 * para estresar el recolector de basura (GC) y forzar al sistema operativo
 * a emitir señales de Low Memory Killer (LMK) / TRIM_MEMORY_CRITICAL.
 */
class MemoryPressureEngine {

    private val TAG = "MemoryPressureEngine"
    private val isRunning = AtomicBoolean(false)
    private val allocatedBuffers = mutableListOf<ByteBuffer>()

    fun startMemoryPressure(targetMegabytes: Int = 400, onProgress: (allocatedMb: Int) -> Unit = {}): Int {
        if (isRunning.getAndSet(true)) return 0
        allocatedBuffers.clear()

        var currentMb = 0
        try {
            while (isRunning.get() && currentMb < targetMegabytes) {
                // Reserva bloques de 10 MB
                val buffer = ByteBuffer.allocateDirect(10 * 1024 * 1024)
                // Llenado con datos para forzar asignación en páginas de memoria física (RSS)
                for (i in 0 until 1024 * 1024 step 64) {
                    buffer.put(i, 0xFF.toByte())
                }
                allocatedBuffers.add(buffer)
                currentMb += 10
                onProgress(currentMb)
                Thread.sleep(150)
            }
        } catch (oom: OutOfMemoryError) {
            Log.w(TAG, "Límite OOM alcanzado con éxito a los $currentMb MB: ${oom.message}")
        }
        return currentMb
    }

    fun releaseMemory() {
        isRunning.set(false)
        allocatedBuffers.clear()
        System.gc()
        Log.i(TAG, "Memoria de estrés liberada.")
    }

    fun isActive(): Boolean = isRunning.get()
}
