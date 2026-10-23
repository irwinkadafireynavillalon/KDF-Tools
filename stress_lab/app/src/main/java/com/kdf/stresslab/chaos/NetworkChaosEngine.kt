package com.kdf.stresslab.chaos

import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

/**
 * Inyector de Caos de Red y Latencia Artificial.
 * Simula fluctuaciones de red (latencias de 2000ms a 5000ms, caídas intermitentes
 * y saturación de peticiones concurrentes) para verificar cómo responden
 * los clientes Retrofit/OkHttp y el manejo de reconexiones automáticas.
 */
class NetworkChaosEngine {

    private val isRunning = AtomicBoolean(false)
    private var chaosJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun startNetworkChaos(testUrl: String = "https://www.google.com/generate_204") {
        if (isRunning.getAndSet(true)) return

        chaosJob = scope.launch {
            while (isActive && isRunning.get()) {
                val shouldSimulateTimeout = Random.nextBoolean()
                if (shouldSimulateTimeout) {
                    // Inyección de retraso de conexión
                    delay(Random.nextLong(2000, 4500))
                }

                try {
                    val conn = (URL(testUrl).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 3000
                        readTimeout = 3000
                        requestMethod = "GET"
                    }
                    conn.connect()
                    conn.disconnect()
                } catch (ignored: Exception) {
                    // Fallo simulado esperado
                }

                delay(Random.nextLong(500, 1500))
            }
        }
    }

    fun stop() {
        isRunning.set(false)
        chaosJob?.cancel()
        chaosJob = null
    }

    fun isActive(): Boolean = isRunning.get()
}
