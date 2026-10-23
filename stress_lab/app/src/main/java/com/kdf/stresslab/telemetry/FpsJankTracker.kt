package com.kdf.stresslab.telemetry

import android.view.Choreographer
import java.util.concurrent.atomic.AtomicInteger

/**
 * Monitor de Fotogramas Caídos (Jank & Dropped Frames Tracker).
 * Utiliza el Choreographer del sistema operativo para medir cada ciclo de renderizado (16.6ms a 60fps).
 * Si un fotograma tarda más de 32ms, se cuenta como "Jank" (congelamiento perceptible).
 */
class FpsJankTracker {

    private var lastFrameTimeNanos: Long = 0
    val totalFrames = AtomicInteger(0)
    val jankFrames = AtomicInteger(0)
    private var isTracking = false

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isTracking) return

            if (lastFrameTimeNanos != 0L) {
                val frameDurationMs = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000
                totalFrames.incrementAndGet()

                // Si excede 32ms (~2 fotogramas perdidos a 60Hz)
                if (frameDurationMs > 32) {
                    jankFrames.incrementAndGet()
                }
            }
            lastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun start() {
        if (isTracking) return
        isTracking = true
        lastFrameTimeNanos = 0
        totalFrames.set(0)
        jankFrames.set(0)
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stop() {
        isTracking = false
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

    fun getJankPercentage(): Double {
        val total = totalFrames.get()
        if (total == 0) return 0.0
        return (jankFrames.get().toDouble() / total) * 100.0
    }
}
