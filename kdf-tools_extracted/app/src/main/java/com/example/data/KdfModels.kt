package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCategoryModule(
    val id: String,
    val title: String,
    val configPath: String,
    val description: String,
    val primaryScripts: List<String>,
    val permissionsRequired: String,
    val supportedBrands: List<String>,
    val supportedProcessors: List<String>,
    val connectionProtocol: String
)

@Serializable
data class MaintenanceOperation(
    val id: String,
    val englishModuleName: String,
    val visibleName: String,
    val cardTitle: String,
    val scriptPath: String,
    val configSection: String,
    val category: String,
    val description: String,
    val permissions: String,
    val requiresModalInput: Boolean = false,
    val supportsWipeUserdata: Boolean = false
)

data class InstalledAppInfo(
    val packageName: String,
    val appLabel: String,
    val isSystemApp: Boolean,
    val versionName: String
)

data class UsbMultimeterReading(
    val voltageVolts: Float = 5.08f,
    val voltageMv: Int = 5080,
    val currentMa: Float = 480f,
    val powerWatts: Float = 2.44f,
    val resistanceOhms: Float = 10.58f,
    val temperatureC: Float = 31.2f,
    val powerSourceLabel: String = "USB Host / Bus Interno",
    val shortCircuitStatus: String = "SIN CORTO • AISLAMIENTO OK",
    val isShortDetected: Boolean = false,
    val vbusLineState: String = "OK (5.08V Nominal)",
    val dPlusLineState: String = "OK (0.62V Datos)",
    val dMinusLineState: String = "OK (0.61V Datos)",
    val ccPinState: String = "OK (5.1kΩ Pull-Down Rd)",
    val gndResistanceMohm: Int = 18,
    val securityAuditSummary: String = "OEM Lock / FRP / EFS verificados"
)

data class LiveHardwareTelemetry(
    val deviceModel: String = "",
    val manufacturer: String = "",
    val androidVersion: String = "",
    val sdkInt: Int = 0,
    val socHardware: String = "",
    val bootloaderVersion: String = "",
    val verifiedBootState: String = "",
    val ramTotalMb: Long = 0L,
    val ramAvailableMb: Long = 0L,
    val ramUsedPercent: Int = 0,
    val storageTotalGb: String = "0 GB",
    val storageFreeGb: String = "0 GB",
    val appCacheBytes: Long = 0L,
    val batteryLevel: Int = 100,
    val batteryTempC: Float = 31.5f,
    val batteryVoltageMv: Int = 4150,
    val usbDevicesCount: Int = 0,
    val usbDevicesSummary: String = "Sin dispositivo OTG externo",
    val activeSensorsCount: Int = 0,
    val geomagneticFieldUt: Float = 45.2f,
    val accelerometerMag: Float = 9.81f,
    val usbMultimeter: UsbMultimeterReading = UsbMultimeterReading()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val moduleId: String,
    val operationName: String,
    val scriptPath: String,
    val commandExecuted: String,
    val outputLog: String,
    val telemetryStatusAfter: String,
    val isSuccess: Boolean
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val prompt: String,
    val imageSize: String,
    val aspectRatio: String,
    val filePath: String,
    val modelUsed: String = "gemini-3-pro-image-preview"
)

@Entity(tableName = "bioscience_records")
data class BioScienceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sampleName: String,
    val sequenceOrSensorData: String,
    val gcContentPercent: Float,
    val meltingTempC: Float,
    val molecularWeightDa: Float,
    val proteinTranslation: String,
    val aiInterpretation: String
)

@Entity(tableName = "autonomous_learning")
data class AutonomousLearningEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val cycleVersion: String,
    val actionType: String, // "AUTO-REPARACIÓN", "AUTO-ACTUALIZACIÓN", "APRENDIZAJE IA"
    val repairedFilesCount: Int,
    val learnedRuleSummary: String,
    val optimizationGainPercent: Int
)

@Dao
interface KdfDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()

    @Query("SELECT * FROM generated_images ORDER BY timestamp DESC")
    fun getAllGeneratedImages(): Flow<List<GeneratedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedImage(image: GeneratedImageEntity)

    @Query("SELECT * FROM bioscience_records ORDER BY timestamp DESC")
    fun getAllBioRecords(): Flow<List<BioScienceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBioRecord(record: BioScienceRecordEntity)

    @Query("SELECT * FROM autonomous_learning ORDER BY timestamp DESC")
    fun getAllAutonomousRecords(): Flow<List<AutonomousLearningEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutonomousRecord(record: AutonomousLearningEntity)
}

@Database(
    entities = [
        AuditLogEntity::class,
        GeneratedImageEntity::class,
        BioScienceRecordEntity::class,
        AutonomousLearningEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KdfDatabase : RoomDatabase() {
    abstract fun kdfDao(): KdfDao

    companion object {
        @Volatile
        private var INSTANCE: KdfDatabase? = null

        fun getInstance(context: Context): KdfDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KdfDatabase::class.java,
                    "kdf_lux_bimetallic.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object KdfArchitectureCatalog {
    // 1. Los 6 Módulos de Dispositivo ("Device Management" - existen desde el principio)
    val deviceModules: List<DeviceCategoryModule> = listOf(
        DeviceCategoryModule(
            id = "smartphones",
            title = "Smartphones",
            configPath = "categories → Smartphones",
            description = "Soporte completo para teléfonos Android móviles: flasheo AOSP GSI, backup de particiones, rescate unbrick y diagnóstico de bajo nivel.",
            primaryScripts = listOf(
                "scripts/flash_aosp_gsi.sh",
                "scripts/unbrick_recovery.sh",
                "scripts/backup_partitions.sh",
                "scripts/low_level_diagnostic.sh",
                "scripts/usb_short_detector.sh"
            ),
            permissionsRequired = "adb/fastboot (acceso al dispositivo; bootloader desbloqueado o modo Fastboot/EDL)",
            supportedBrands = listOf("Samsung", "Xiaomi / POCO", "Google Pixel", "Motorola", "OnePlus", "ASUS ROG", "Nothing", "Realme", "Sony Xperia"),
            supportedProcessors = listOf("Qualcomm Snapdragon 8 Gen 3 / 8 Elite", "MediaTek Dimensity 9300+", "Samsung Exynos 2400", "Google Tensor G4", "Unisoc T820"),
            connectionProtocol = "USB-C ADB / Fastbootd / Qualcomm EDL 9008"
        ),
        DeviceCategoryModule(
            id = "tablets",
            title = "Tablets",
            configPath = "categories → Tablets",
            description = "Soporte para tablets Android: particionado dinámico super.img, backup NVRAM/EFS, calibración multi-ventana y flasheo GSI.",
            primaryScripts = listOf(
                "scripts/flash_aosp_gsi.sh",
                "scripts/unbrick_recovery.sh",
                "scripts/backup_partitions.sh",
                "scripts/low_level_diagnostic.sh",
                "scripts/usb_short_detector.sh"
            ),
            permissionsRequired = "adb/fastboot (sin acceso root requerido)",
            supportedBrands = listOf("Samsung Galaxy Tab", "Xiaomi Pad", "Lenovo Tab Extreme", "Google Pixel Tablet", "OnePlus Pad"),
            supportedProcessors = listOf("Snapdragon 8 Gen 2/3", "Dimensity 9000+", "Google Tensor G2", "Snapdragon 870"),
            connectionProtocol = "ADB Multi-Display / Fastboot Dual-Slot (A/B)"
        ),
        DeviceCategoryModule(
            id = "laptops",
            title = "Laptops & PC",
            configPath = "categories → Laptops & Computadoras",
            description = "Soporte para laptops/PC con Android-x86, BlissOS, Chromebook Crostini o subsistema Windows-ADB.",
            primaryScripts = listOf(
                "scripts/flash_aosp_gsi.sh",
                "scripts/unbrick_recovery.sh",
                "scripts/backup_partitions.sh",
                "scripts/low_level_diagnostic.sh",
                "scripts/usb_short_detector.sh"
            ),
            permissionsRequired = "adb over TCP/IP o USB-Bridge (sin root)",
            supportedBrands = listOf("ASUS Chromebook", "HP x86_64", "Lenovo ThinkPad Android-x86", "Surface WSA Bridge", "Framework Laptop"),
            supportedProcessors = listOf("Intel Core Ultra x86_64", "AMD Ryzen AI 9", "Qualcomm Snapdragon X Elite", "MediaTek Kompanio"),
            connectionProtocol = "ADB TCP/IP :5555 / UEFI Fastboot"
        ),
        DeviceCategoryModule(
            id = "smart_tv",
            title = "Smart TV",
            configPath = "categories → Smart TVs (Android/Google TV)",
            description = "Soporte para Smart TV Android/Google TV: optimización Leanback, backup de boot/recovery y rescate por USB.",
            primaryScripts = listOf(
                "scripts/flash_aosp_gsi.sh",
                "scripts/unbrick_recovery.sh",
                "scripts/backup_partitions.sh",
                "scripts/low_level_diagnostic.sh",
                "scripts/usb_short_detector.sh"
            ),
            permissionsRequired = "adb inalámbrico o USB A-A / Fastboot bootloader",
            supportedBrands = listOf("Sony Bravia", "TCL Google TV", "Hisense", "Nvidia Shield TV Pro", "Xiaomi TV Box S"),
            supportedProcessors = listOf("Amlogic S905X4", "MediaTek Pentonic 1000", "Nvidia Tegra X1+", "Realtek RTD1319"),
            connectionProtocol = "ADB LAN :5555 / Amlogic USB Burning Protocol"
        ),
        DeviceCategoryModule(
            id = "wearables",
            title = "Wearables",
            configPath = "categories → Smartwatch (Wearables)",
            description = "Soporte para smartwatches Wear OS y wearables: depuración Wi-Fi/Bluetooth, backup y calibración de sensores.",
            primaryScripts = listOf(
                "scripts/flash_aosp_gsi.sh",
                "scripts/unbrick_recovery.sh",
                "scripts/backup_partitions.sh",
                "scripts/low_level_diagnostic.sh",
                "scripts/usb_short_detector.sh"
            ),
            permissionsRequired = "adb wireless debugging (par de emparejamiento Wear OS)",
            supportedBrands = listOf("Samsung Galaxy Watch Ultra", "Google Pixel Watch 3", "OnePlus Watch 2", "TicWatch Pro 5", "Xiaomi Watch 2 Pro"),
            supportedProcessors = listOf("Exynos W1000", "Snapdragon W5+ Gen 1", "Beken Co-Processor"),
            connectionProtocol = "Wireless ADB Pair / Pogo-Pin Fastboot"
        ),
        DeviceCategoryModule(
            id = "automotriz",
            title = "Automotriz",
            configPath = "categories → Automotriz (Infotainment & Android Auto)",
            description = "Soporte para unidades de infotainment Android Automotive OS (AAOS) y consolas Android Auto.",
            primaryScripts = listOf(
                "scripts/flash_aosp_gsi.sh",
                "scripts/unbrick_recovery.sh",
                "scripts/backup_partitions.sh",
                "scripts/low_level_diagnostic.sh",
                "scripts/usb_short_detector.sh"
            ),
            permissionsRequired = "adb/fastboot (modo ingeniería de unidad principal USB Host)",
            supportedBrands = listOf("Polestar AAOS", "Volvo Infotainment", "Renault OpenR", "Pioneer NEX", "Sony XAV", "Qualcomm Snapdragon Cockpit"),
            supportedProcessors = listOf("Snapdragon Ride / Cockpit SA8295P", "NXP i.MX 8QuadMax", "UIS7862 Octa-Core"),
            connectionProtocol = "USB OTG Host Bridge / OBD-II CAN Telemetry"
        )
    )

    // 2. Módulos de Utilidad / Herramientas de Mantenimiento (aplicables a cualquier categoría)
    val maintenanceOperations: List<MaintenanceOperation> = listOf(
        MaintenanceOperation(
            id = "mode_safe",
            englishModuleName = "Safe Mode Manager",
            visibleName = "Modo seguro",
            cardTitle = "Entrar en modo seguro",
            scriptPath = "scripts/enter_safe_mode.sh",
            configSection = "operations → mode_safe en categories.yaml",
            category = "Mantenimiento",
            description = "Activa modo seguro de Android (persist.sys.safemode=1) y reinicia para desactivar apps de terceros.",
            permissions = "adb (solo lectura/escritura de propiedades del sistema, sin root)",
            requiresModalInput = false,
            supportsWipeUserdata = false
        ),
        MaintenanceOperation(
            id = "app_blocker",
            englishModuleName = "App Blocker",
            visibleName = "Desactivar / eliminar app bloqueadora",
            cardTitle = "Desactivar / eliminar aplicación bloqueadora",
            scriptPath = "scripts/disable_blocker.sh",
            configSection = "operations → app_blocker en categories.yaml",
            category = "Mantenimiento",
            description = "Entra en modo seguro, desactiva (pm disable-user) o desinstala (pm uninstall) el paquete bloqueador y opcionalmente ejecuta fastboot -w.",
            permissions = "adb (pm) + opcional fastboot -w",
            requiresModalInput = true,
            supportsWipeUserdata = true
        ),
        MaintenanceOperation(
            id = "mem_cleaner",
            englishModuleName = "Memory Cleaner",
            visibleName = "Limpieza de memoria / caché",
            cardTitle = "Limpiar cachés y memoria",
            scriptPath = "scripts/clean_memory.sh",
            configSection = "operations → mem_cleaner en categories.yaml",
            category = "Mantenimiento",
            description = "Borra /data/cache, /cache, libera procesos en RAM y, si se confirma, ejecuta fastboot -w para wipe userdata.",
            permissions = "adb (rm) + opcional fastboot -w (solo con confirmación)",
            requiresModalInput = true,
            supportsWipeUserdata = true
        ),
        MaintenanceOperation(
            id = "bootloader_unlock",
            englishModuleName = "Bootloader Unlock",
            visibleName = "Desbloquear bootloader / flashear bootloader",
            cardTitle = "Desbloquear bootloader",
            scriptPath = "scripts/unlock_bootloader.sh",
            configSection = "operations → bootloader_unlock",
            category = "Bootloader",
            description = "Ejecuta fastboot oem unlock o fastboot flashing unlock verificando el estado del OEM unlock en el dispositivo.",
            permissions = "fastboot (requiere dispositivo en modo Fastboot)",
            requiresModalInput = false,
            supportsWipeUserdata = false
        ),
        MaintenanceOperation(
            id = "edl_flasher",
            englishModuleName = "EDL Flasher (9008)",
            visibleName = "Flashear bootloader desde modo EDL",
            cardTitle = "Flashear bootloader vía EDL",
            scriptPath = "scripts/edl_flasher.sh",
            configSection = "operations → edl_flasher",
            category = "Bootloader",
            description = "Usa edl.py (o QFIL) para escribir bootloader.img y radio.img cuando el dispositivo está en modo EDL (test-point 9008).",
            permissions = "edl (requiere acceso USB al chipset Qualcomm)",
            requiresModalInput = true,
            supportsWipeUserdata = false
        ),
        MaintenanceOperation(
            id = "usb_multimeter_short",
            englishModuleName = "USB Multimeter & Short Detector",
            visibleName = "Multímetro USB y Detector de Cortos",
            cardTitle = "Revisar cortos en puerto USB (VBUS / D+ / D- / CC)",
            scriptPath = "scripts/usb_short_detector.sh",
            configSection = "operations → usb_multimeter_short",
            category = "Diagnóstico Eléctrico",
            description = "Mide voltaje (V), corriente (mA), potencia (W), resistencia de aislamiento (Ω) y detecta sobrecorrientes o cortos en líneas USB.",
            permissions = "BatteryManager / UsbManager / Kernel Power Supply",
            requiresModalInput = false,
            supportsWipeUserdata = false
        ),
        MaintenanceOperation(
            id = "autonomous_self_heal",
            englishModuleName = "Autonomous Self-Healing & Learning",
            visibleName = "Auto-reparación, actualización y aprendizaje autónomo",
            cardTitle = "Ejecutar núcleo autónomo (Auto-Reparar y Actualizar)",
            scriptPath = "scripts/self_heal_autonomous.sh",
            configSection = "operations → autonomous_self_heal",
            category = "Núcleo Autónomo",
            description = "Verifica y repara scripts/YAML dañados, actualiza reglas operativas y entrena la memoria adaptativa de KDF-Tools.",
            permissions = "Sistema de archivos interno + Motor IA KDF-LuX",
            requiresModalInput = false,
            supportsWipeUserdata = false
        ),
        MaintenanceOperation(
            id = "gpu_npu_upscaler",
            englishModuleName = "GPU/NPU Super-Resolution & Computational ISP",
            visibleName = "Superresolución GPU/NPU & Fotografía Computacional",
            cardTitle = "Activar Superresolución (GSR/FSR/DLSS) + HDR ISP + NNAPI",
            scriptPath = "scripts/gpu_npu_upscaler.sh",
            configSection = "operations → gpu_npu_upscaler",
            category = "Aceleración Visual",
            description = "Escala imágenes y gráficos usando GPU/NPU/DSP (Snapdragon GSR, AMD FSR, MetalFX), aplica HDR/Denoise del ISP y acelera con NNAPI/AICore.",
            permissions = "GPU SkiaVK / Android NNAPI / AICore / ISP Hardware",
            requiresModalInput = false,
            supportsWipeUserdata = false
        ),
        MaintenanceOperation(
            id = "contextual_heuristics_engine",
            englishModuleName = "Contextual Heuristics, Eliza Engine & AI Washing Auditor",
            visibleName = "Heurísticas, Efecto Eliza, Smart Features y Auditor AI Washing",
            cardTitle = "Ejecutar Motor de Reglas, Sensores Contextuales y Auditoría IA",
            scriptPath = "scripts/contextual_heuristics_engine.sh",
            configSection = "operations → contextual_heuristics_engine",
            category = "Heurística & Smart OS",
            description = "Ejecuta sistemas basados en reglas (if/else + filtros estadísticos), motor conversacional Efecto Eliza, automatización por sensores y detector de AI Washing.",
            permissions = "Sensores del SO (Batería, Red, Hora, RAM) + Motor de Reglas",
            requiresModalInput = false,
            supportsWipeUserdata = false
        )
    )
}
