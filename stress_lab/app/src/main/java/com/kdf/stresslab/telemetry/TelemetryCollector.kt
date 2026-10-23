package com.kdf.stresslab.telemetry

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Process

/**
 * Colector de Telemetría en Vivo.
 * Mide el consumo de memoria PSS (Proportional Set Size), el tamaño del Heap Dalvik
 * y la cantidad de memoria libre del sistema para evaluar el impacto del estrés.
 */
class TelemetryCollector(private val context: Context) {

    data class MemorySnapshot(
        val totalPssMb: Long,
        val dalvikPssMb: Long,
        val nativePssMb: Long,
        val heapAllocatedMb: Long,
        val systemAvailableMb: Long,
        val isLowMemory: Boolean
    )

    fun captureMemorySnapshot(): MemorySnapshot {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        val pids = intArrayOf(Process.myPid())
        val pssArray = am.getProcessMemoryInfo(pids)
        val processMem = pssArray.firstOrNull() ?: Debug.MemoryInfo()

        val runtime = Runtime.getRuntime()
        val heapAllocated = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

        return MemorySnapshot(
            totalPssMb = processMem.totalPss.toLong() / 1024,
            dalvikPssMb = processMem.dalvikPss.toLong() / 1024,
            nativePssMb = processMem.nativePss.toLong() / 1024,
            heapAllocatedMb = heapAllocated,
            systemAvailableMb = memInfo.availMem / (1024 * 1024),
            isLowMemory = memInfo.lowMemory
        )
    }
}
