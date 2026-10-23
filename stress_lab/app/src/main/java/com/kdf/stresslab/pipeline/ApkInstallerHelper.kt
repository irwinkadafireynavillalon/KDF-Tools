package com.kdf.stresslab.pipeline

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Gestor de Extracción e Instalación de APK / ZIP.
 * Permite recibir un APK o un archivo ZIP con APK dentro, extraerlo en la caché,
 * leer sus metadatos (Package Name, SDK, Permisos críticos) y detonar su instalación.
 */
class ApkInstallerHelper(private val context: Context) {

    private val TAG = "ApkInstallerHelper"

    data class ExtractedApkMetadata(
        val apkFile: File,
        val packageName: String,
        val appName: String,
        val versionCode: Long,
        val versionName: String,
        val targetSdk: Int,
        val minSdk: Int,
        val requestedPermissions: List<String>,
        val dangerousPermissions: List<String>
    )

    fun processUploadedUri(uri: Uri): ExtractedApkMetadata? {
        val tempDir = File(context.cacheDir, "uploaded_test_app").apply {
            deleteRecursively()
            mkdirs()
        }

        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: ""
        val fileName = getFileName(uri)

        val targetApk: File = if (fileName.endsWith(".zip", ignoreCase = true) || mimeType.contains("zip")) {
            // Descomprimir ZIP y buscar el primer APK
            val extracted = extractApkFromZip(contentResolver.openInputStream(uri)!!, tempDir)
            extracted ?: return null
        } else {
            // Copiar APK directo
            val destFile = File(tempDir, "target_under_test.apk")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output -> input.copyTo(output) }
            }
            destFile
        }

        return inspectApk(targetApk)
    }

    private fun extractApkFromZip(inputStream: InputStream, destDir: File): File? {
        ZipInputStream(inputStream).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (entry.name.endsWith(".apk", ignoreCase = true) && !entry.isDirectory) {
                    val apkFile = File(destDir, "extracted_target.apk")
                    FileOutputStream(apkFile).use { fos -> zis.copyTo(fos) }
                    return apkFile
                }
                entry = zis.nextEntry
            }
        }
        return null
    }

    private fun inspectApk(apkFile: File): ExtractedApkMetadata? {
        val pm = context.packageManager
        val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
        val packageInfo: PackageInfo = pm.getPackageArchiveInfo(apkFile.absolutePath, flags) ?: run {
            Log.e(TAG, "No se pudo parsear el archivo APK: ${apkFile.name}")
            return null
        }

        packageInfo.applicationInfo?.sourceDir = apkFile.absolutePath
        packageInfo.applicationInfo?.publicSourceDir = apkFile.absolutePath

        val appLabel = packageInfo.applicationInfo?.let { pm.getApplicationLabel(it).toString() } ?: apkFile.nameWithoutExtension
        val vCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }

        val allPerms = packageInfo.requestedPermissions?.toList() ?: emptyList()
        val dangerousList = allPerms.filter { isDangerousPermission(it) }

        return ExtractedApkMetadata(
            apkFile = apkFile,
            packageName = packageInfo.packageName,
            appName = appLabel,
            versionCode = vCode,
            versionName = packageInfo.versionName ?: "1.0",
            targetSdk = packageInfo.applicationInfo?.targetSdkVersion ?: 0,
            minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) packageInfo.applicationInfo?.minSdkVersion ?: 0 else 0,
            requestedPermissions = allPerms,
            dangerousPermissions = dangerousList
        )
    }

    private fun isDangerousPermission(perm: String): Boolean {
        val sensitivePrefixes = listOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.QUERY_ALL_PACKAGES",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.KILL_BACKGROUND_PROCESSES"
        )
        return sensitivePrefixes.any { perm.contains(it) }
    }

    fun triggerInstallation(apkFile: File) {
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(installIntent)
    }

    private fun getFileName(uri: Uri): String {
        return uri.lastPathSegment ?: "unknown_file"
    }
}
