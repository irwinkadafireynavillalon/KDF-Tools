package com.kdf.stresslab.sandbox

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

/**
 * Gestor del Entorno Sandbox & Contenedor de Pruebas.
 * Permite instanciar, consultar y lanzar paquetes objetivo en un espacio
 * de observación controlado para inyectar sobrecargas y caos del sistema.
 */
class AppSandboxManager(private val context: Context) {

    private val TAG = "AppSandboxManager"

    data class TargetAppInfo(
        val packageName: String,
        val appName: String,
        val versionName: String,
        val isSystemApp: Boolean
    )

    fun getInstalledTargetApps(): List<TargetAppInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        return packages.mapNotNull { pkg ->
            try {
                val appInfo = pkg.applicationInfo ?: return@mapNotNull null
                val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                TargetAppInfo(
                    packageName = pkg.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    versionName = pkg.versionName ?: "1.0",
                    isSystemApp = isSystem
                )
            } catch (e: Exception) {
                null
            }
        }.filter { !it.isSystemApp } // Prioriza aplicaciones de usuario para pruebas
    }

    fun launchInSandbox(packageName: String): Boolean {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            Log.i(TAG, "App objetivo $packageName lanzada bajo supervisión de KDF-StressLab")
            true
        } else {
            Log.e(TAG, "No se encontró intent de lanzamiento para $packageName")
            false
        }
    }
}
