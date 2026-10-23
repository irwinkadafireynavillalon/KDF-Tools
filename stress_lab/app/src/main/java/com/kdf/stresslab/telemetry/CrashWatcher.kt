package com.kdf.stresslab.telemetry

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Watchdog de Cuelgues (ANR) y Capturador de Excepciones Fatales.
 * 1. Monitorea el hilo principal (Main Looper) enviando un "latido" (heartbeat).
 *    Si el hilo UI no responde en 5000 ms, se declara un ANR (Application Not Responding).
 * 2. Intercepta excepciones no controladas mediante UncaughtExceptionHandler.
 */
class CrashWatcher(
    private val onCrashDetected: (thread: Thread, throwable: Throwable) -> Unit,
    private val onAnrDetected: () -> Unit
) {

    private val TAG = "CrashWatcher"
    val crashCount = AtomicInteger(0)
    val anrCount = AtomicInteger(0)
    private val isRunning = AtomicBoolean(false)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var watchdogThread: Thread? = null

    fun install() {
        if (isRunning.getAndSet(true)) return

        // 1. Interceptar Crashes Fatales
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            crashCount.incrementAndGet()
            Log.e(TAG, "FATAL CRASH DETECTADO: ${throwable.message}", throwable)
            onCrashDetected(thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // 2. Watchdog de ANR (Heartbeat de 5 segundos)
        watchdogThread = Thread({
            while (isRunning.get()) {
                val completed = AtomicBoolean(false)
                mainHandler.post { completed.set(true) }

                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    break
                }

                if (!completed.get() && isRunning.get()) {
                    anrCount.incrementAndGet()
                    Log.w(TAG, "ANR DETECTADO: El hilo principal ha estado bloqueado por más de 5000 ms")
                    onAnrDetected()
                }
            }
        }, "KDF-ANR-Watchdog").apply { start() }
    }

    fun uninstall() {
        isRunning.set(false)
        watchdogThread?.interrupt()
        watchdogThread = null
    }
}
