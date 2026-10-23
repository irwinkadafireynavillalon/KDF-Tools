package com.kdf.stresslab.chaos

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Motor de Estrés de CPU Multi-Core.
 * Lanza hilos de cálculo intensivo (trigonométrico, primos y matrices)
 * saturando todos los núcleos disponibles (ej. 8 núcleos en Moto G24)
 * para provocar estrangulamiento térmico (Thermal Throttling) y retrasos en hilos UI.
 */
class CpuStressEngine {

    private val isRunning = AtomicBoolean(false)
    private var stressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun startCpuStress(coresToSaturate: Int = Runtime.getRuntime().availableProcessors()): Boolean {
        if (isRunning.getAndSet(true)) return false

        stressJob = scope.launch {
            val jobs = List(coresToSaturate) { coreIndex ->
                launch {
                    var counter = 0.0
                    while (isActive && isRunning.get()) {
                        // Carga intensiva de punto flotante y cálculo matemático
                        counter += sin(counter) * sqrt(counter + 1.0)
                        if (counter > 1_000_000.0) counter = 0.1
                    }
                }
            }
            jobs.joinAll()
        }
        return true
    }

    fun stop() {
        isRunning.set(false)
        stressJob?.cancel()
        stressJob = null
    }

    fun isActive(): Boolean = isRunning.get()
}
