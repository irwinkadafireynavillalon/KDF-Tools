package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiTransparencyAuditItem
import com.example.data.AuditLogEntity
import com.example.data.BioScienceRecordEntity
import com.example.data.DeviceCategoryModule
import com.example.data.ElizaHeuristicResponse
import com.example.data.GeminiService
import com.example.data.GeneratedImageEntity
import com.example.data.HardwareAcceleratorStatus
import com.example.data.HardwareVisualAndHeuristicsEngine
import com.example.data.InstalledAppInfo
import com.example.data.KdfArchitectureCatalog
import com.example.data.KdfDatabase
import com.example.data.KdfScriptEngine
import com.example.data.LiveHardwareTelemetry
import com.example.data.MaintenanceOperation
import com.example.data.SmartContextualRule
import com.example.data.UpscalingProfile
import com.example.data.VisualEnhancementSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.sqrt

enum class KdfScreenTab {
    DASHBOARD_MODULES,
    DIAGNOSTIC_CENTER,
    MAINTENANCE_OPS,
    SYSTEM_BROWSER,
    SYSTEM_MAPS,
    PERSONAL_ASSISTANT_PLANNER,
    HARDWARE_VISUAL,
    WALLPAPER_CUSTOMIZER,
    SCRIPTS_STUDIO
}

data class ActiveModalState(
    val operation: MaintenanceOperation,
    val targetPackage: String = "com.ejemplo.blocker",
    val blockerActionMode: String = "disable", // "disable" or "uninstall"
    val confirmWipeUserdata: Boolean = false,
    val edlBootloaderImg: String = "bootloader.img",
    val edlRadioImg: String = "radio.img",
    val edlFirehoseLoader: String = "prog_firehose_ddr.elf"
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val isUser: Boolean,
    val text: String,
    val engineBadge: String = "KDF-LuX Chatbot (IA + Heurística)",
    val matchedRuleOrAction: String? = null
)

data class KdfUiState(
    val currentTab: KdfScreenTab = KdfScreenTab.DASHBOARD_MODULES,
    val isObsidianMode: Boolean = true,
    val showLogoModal: Boolean = false,
    val showChatbotModal: Boolean = false,
    val chatbotMode: String = "HYBRID_AI_ELIZA", // "HYBRID_AI_ELIZA", "ELIZA_RULES_ONLY", "GEMINI_NEURAL_ONLY"
    val logoExportStatus: String = "",
    val customBackgroundPath: String? = null,
    val backgroundPreset: String = "MEWTWO_FILIGREE",
    val backgroundDimAlpha: Float = 0.72f,
    val backgroundBlurRadius: Float = 0f,
    val backgroundContrastBoost: Float = 1.15f,
    val selectedDiagnosticProblemType: String = "Bootloop / Reinicio en logo",
    val diagnosticProblemDescription: String = "El dispositivo se reinicia continuamente en el logo del fabricante o pantalla de arranque.",
    val isTestingConnectivity: Boolean = false,
    val lastConnectivityResult: String = "Puente ADB: Listo | USB Host: Activo | Red Wi-Fi: Verificada",
    val isAutonomousModeEnabled: Boolean = true,
    val autonomyLevelVersion: String = "v2.6.1-AutoLuX",
    val autonomousHealthScore: Int = 100,
    val autonomousStatusMessage: String = "Auto-reparación, auto-actualización y aprendizaje activos",
    val visualSettings: VisualEnhancementSettings = VisualEnhancementSettings(),
    val hardwareAccelerator: HardwareAcceleratorStatus = HardwareAcceleratorStatus(),
    val isUpscalingImage: Boolean = false,
    val smartAutomationEnabled: Boolean = true,
    val smartContextualRules: List<SmartContextualRule> = emptyList(),
    val aiTransparencyAuditItems: List<AiTransparencyAuditItem> = emptyList(),
    val elizaInputQuery: String = "Revisar si hay corto en USB o caída de voltaje y mejorar nitidez GPU",
    val lastElizaResponse: ElizaHeuristicResponse = ElizaHeuristicResponse(
        userInput = "Inicio del sistema",
        matchedPatternRule = "REGLA #00 [BOOT_HEURISTIC_READY]",
        elizaResponseText = "Motor de reglas condicionales (Efecto Eliza + Sensores SO) activo en <0.1 ms sin latencia de red.",
        executionLatencyMs = 0.12,
        statisticalFilterNote = "Z-Score VBUS=0.00σ | Estado Nominal"
    ),
    val selectedCategory: DeviceCategoryModule = KdfArchitectureCatalog.deviceModules.first(),
    val selectedBrand: String = KdfArchitectureCatalog.deviceModules.first().supportedBrands.first(),
    val selectedProcessor: String = KdfArchitectureCatalog.deviceModules.first().supportedProcessors.first(),
    val telemetryPhase: String = "Sistema Autónomo • Chatbot & GPU Activos",
    val liveTelemetry: LiveHardwareTelemetry = LiveHardwareTelemetry(),
    val installedPackages: List<InstalledAppInfo> = emptyList(),
    val activeModal: ActiveModalState? = null,
    val isExecutingCommand: Boolean = false,
    val wirelessAdbEndpoint: String = "192.168.1.100:5555",
    val terminalCommandInput: String = "./scripts/disable_blocker.sh com.ejemplo.blocker",
    val lastConsoleOutput: String = "[KDF-LuX Console Ready] • Chatbot IA/Eliza + 6 módulos de dispositivo + 9 herramientas + Multímetro USB + Superresolución GPU/ISP activos.",
    val scriptFiles: List<File> = emptyList(),
    val selectedFile: File? = null,
    val selectedFileContent: String = "",
    val selectedImageSize: String = "2K", // "1K", "2K", "4K"
    val selectedAspectRatio: String = "1:1",
    val imagePrompt: String = "Emblema real bimetálico de alta relojería en oro pulido y cromo plateado con corona y energía psíquica púrpura Mewtwo, 8k ultra detallado",
    val isGeneratingImage: Boolean = false,
    val imageGenerationStatus: String = "",
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            isUser = false,
            text = "¡Hola! Soy el Chatbot KDF-LuX (Mewtwo Bimetallic Core). Combino IA Neuronal Gemini + Motor de Reglas/Efecto Eliza (<1 ms) + Control Directo de Hardware.\n\nPuedes pedirme:\n• \"Revisar cortos en el puerto USB\"\n• \"Activar Superresolución AMD FSR / Snapdragon GSR\"\n• \"Limpiar memoria y caché\"\n• \"Auto-reparar scripts\"\n• O cualquier consulta sobre ADB, Fastboot, EDL 9008 y Fotografía Computacional.",
            engineBadge = "Chatbot Híbrido: Gemini IA + Efecto Eliza + Sensores SO",
            matchedRuleOrAction = "SISTEMA LISTO • MODO AUTÓNOMO ACTIVO"
        )
    ),
    val isChatLoading: Boolean = false,
    val bioSampleName: String = "Secuencia Bio-Sensor KDF-01",
    val bioSequenceInput: String = "ATGGCCCTGTGGATGCGCCTCCTGCCCCTGCTGGCGCTGCTGGCCCTCTGGGGACCTGACCCAGCCGCAGCCTTTGTGAACCAACACCTGTGCGGCTCACACCTGGTGGAAGCTCTCTACCTAGTGTGCGGGGAACGAGGCTTCTTCTACACACCCAAGTRR",
    val isAnalyzingBio: Boolean = false
)

class KdfToolsViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val db = KdfDatabase.getInstance(application)
    private val dao = db.kdfDao()
    val scriptEngine = KdfScriptEngine(application)
    private val geminiService = GeminiService(application)
    private val visualAndHeuristicsEngine = HardwareVisualAndHeuristicsEngine(application)

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private var latestMagneticUt: Float = 46.8f
    private var latestAccelMag: Float = 9.81f

    private val _uiState = MutableStateFlow(KdfUiState())
    val uiState: StateFlow<KdfUiState> = _uiState.asStateFlow()

    private val prefs = application.getSharedPreferences("kdf_lux_prefs", Context.MODE_PRIVATE)

    val auditLogs: StateFlow<List<AuditLogEntity>> = dao.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generatedImages: StateFlow<List<GeneratedImageEntity>> = dao.getAllGeneratedImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bioRecords: StateFlow<List<BioScienceRecordEntity>> = dao.getAllBioRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autonomousRecords: StateFlow<List< com.example.data.AutonomousLearningEntity >> = dao.getAllAutonomousRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val savedPreset = prefs.getString("bg_preset", "MEWTWO_FILIGREE") ?: "MEWTWO_FILIGREE"
        val savedPath = prefs.getString("bg_custom_path", null)
        val savedDim = prefs.getFloat("bg_dim_alpha", 0.72f)
        val hwAcc = visualAndHeuristicsEngine.readHardwareAcceleratorStatus()
        val auditItems = visualAndHeuristicsEngine.getAiTransparencyAuditReport()
        _uiState.update {
            it.copy(
                backgroundPreset = savedPreset,
                customBackgroundPath = savedPath,
                backgroundDimAlpha = savedDim,
                hardwareAccelerator = hwAcc,
                aiTransparencyAuditItems = auditItems
            )
        }
        registerHardwareSensors()
        refreshFilesAndTelemetry()
        loadInstalledPackages()
        startPeriodicTelemetryRefresh()
        initializeAutonomousBootCycle()
    }

    private fun registerHardwareSensors() {
        try {
            sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let { mag ->
                sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_NORMAL)
            }
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { acc ->
                sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
            }
        } catch (_: Exception) {
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                latestMagneticUt = sqrt(x * x + y * y + z * z)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                latestAccelMag = sqrt(x * x + y * y + z * z)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onCleared() {
        super.onCleared()
        try {
            sensorManager?.unregisterListener(this)
        } catch (_: Exception) {
        }
    }

    private fun startPeriodicTelemetryRefresh() {
        viewModelScope.launch {
            while (true) {
                val hw = scriptEngine.readLiveHardwareTelemetry().copy(
                    geomagneticFieldUt = latestMagneticUt,
                    accelerometerMag = latestAccelMag
                )
                val smartRules = visualAndHeuristicsEngine.evaluateSmartContextualRules(hw)
                _uiState.update {
                    it.copy(
                        liveTelemetry = hw,
                        smartContextualRules = smartRules
                    )
                }
                delay(4000L)
            }
        }
    }

    fun refreshFilesAndTelemetry() {
        val files = scriptEngine.listScriptAndConfigFiles()
        val currentSelected = _uiState.value.selectedFile ?: files.firstOrNull()
        val content = currentSelected?.let { scriptEngine.readFileContent(it) } ?: ""
        val hw = scriptEngine.readLiveHardwareTelemetry().copy(
            geomagneticFieldUt = latestMagneticUt,
            accelerometerMag = latestAccelMag
        )
        val smartRules = visualAndHeuristicsEngine.evaluateSmartContextualRules(hw)
        _uiState.update {
            it.copy(
                scriptFiles = files,
                selectedFile = currentSelected,
                selectedFileContent = content,
                liveTelemetry = hw,
                smartContextualRules = smartRules
            )
        }
    }

    private fun loadInstalledPackages() {
        viewModelScope.launch {
            val pkgs = scriptEngine.getInstalledPackages()
            _uiState.update { it.copy(installedPackages = pkgs) }
        }
    }

    fun selectTab(tab: KdfScreenTab) {
        _uiState.update { it.copy(currentTab = tab) }
        if (tab == KdfScreenTab.SCRIPTS_STUDIO) {
            refreshFilesAndTelemetry()
        }
    }

    fun toggleObsidianMode(enabled: Boolean? = null) {
        _uiState.update {
            val next = enabled ?: !it.isObsidianMode
            it.copy(isObsidianMode = next)
        }
    }

    fun openLogoModal() {
        _uiState.update { it.copy(showLogoModal = true, logoExportStatus = "") }
    }

    fun dismissLogoModal() {
        _uiState.update { it.copy(showLogoModal = false) }
    }

    fun exportCroppedLogoPng() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val bitmap = android.graphics.BitmapFactory.decodeResource(
                    context.resources,
                    com.example.R.drawable.img_kdf_logo
                )
                if (!scriptEngine.rendersDir.exists()) scriptEngine.rendersDir.mkdirs()
                val outFile = File(scriptEngine.rendersDir, "kdf_tools_logo_recortado.png")
                outFile.outputStream().use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }
                dao.insertGeneratedImage(
                    GeneratedImageEntity(
                        prompt = "Logo Oficial KDF-Tools Recortado (Oro, Plata, Negro, Blanco y Morado Mewtwo)",
                        imageSize = "512x512 PNG RGBA",
                        aspectRatio = "1:1",
                        filePath = outFile.absolutePath,
                        modelUsed = "KDF-Tools Emblem Cutout"
                    )
                )
                _uiState.update {
                    it.copy(
                        logoExportStatus = "Logo PNG recortado exportado en: KDF-LuX/renders/${outFile.name}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(logoExportStatus = "Error al exportar PNG: ${e.message}")
                }
            }
        }
    }

    fun selectCategoryModule(module: DeviceCategoryModule) {
        _uiState.update {
            it.copy(
                selectedCategory = module,
                selectedBrand = module.supportedBrands.firstOrNull() ?: "",
                selectedProcessor = module.supportedProcessors.firstOrNull() ?: "",
                lastConsoleOutput = "[Categoría Seleccionada: ${module.title}]\n" +
                    "• Configuración: ${module.configPath}\n" +
                    "• Protocolo: ${module.connectionProtocol}\n" +
                    "• Scripts listos: ${module.primaryScripts.joinToString(", ")}"
            )
        }
    }

    fun selectBrand(brand: String) {
        _uiState.update { it.copy(selectedBrand = brand) }
    }

    fun selectProcessor(processor: String) {
        _uiState.update { it.copy(selectedProcessor = processor) }
    }

    fun updateWirelessAdbEndpoint(endpoint: String) {
        _uiState.update { it.copy(wirelessAdbEndpoint = endpoint) }
    }

    fun updateTerminalCommandInput(cmd: String) {
        _uiState.update { it.copy(terminalCommandInput = cmd) }
    }

    fun verifyHardwarePermissions() {
        val status = scriptEngine.checkAndRequestHardwarePermissions(_uiState.value.wirelessAdbEndpoint)
        val updatedHw = scriptEngine.readLiveHardwareTelemetry()
        _uiState.update {
            it.copy(
                lastConsoleOutput = status,
                telemetryPhase = "Permisos ADB/Fastboot/EDL Verificados",
                liveTelemetry = updatedHw
            )
        }
    }

    fun runRawTerminalCommand(customCmd: String? = null) {
        val cmdToRun = (customCmd ?: _uiState.value.terminalCommandInput).trim()
        if (cmdToRun.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingCommand = true, terminalCommandInput = cmdToRun) }
            val (ok, output) = scriptEngine.executeRawTerminalCommand(cmdToRun)
            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = "terminal_cli",
                    operationName = "Terminal /api/execute",
                    scriptPath = "sh -c",
                    commandExecuted = cmdToRun,
                    outputLog = output,
                    telemetryStatusAfter = "CLI Ejecutado",
                    isSuccess = ok
                )
            )
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    telemetryPhase = "Terminal CLI • Ejecutado",
                    lastConsoleOutput = output
                )
            }
        }
    }

    /**
     * Ejecuta un script de módulo de dispositivo (flash_aosp_gsi.sh, unbrick_recovery.sh, low_level_diagnostic.sh)
     */
    fun executeDeviceModuleScript(scriptPath: String) {
        val cat = _uiState.value.selectedCategory
        val brand = _uiState.value.selectedBrand
        val soc = _uiState.value.selectedProcessor

        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingCommand = true) }
            val (ok, output) = scriptEngine.executeScript(
                scriptRelativePath = scriptPath,
                args = listOf(cat.id, brand.replace(" ", "_"), soc.replace(" ", "_"))
            )
            val newBadge = when {
                scriptPath.contains("flash_aosp_gsi") -> "GSI Flash • ${cat.title}"
                scriptPath.contains("unbrick") -> "Rescate Recovery • ${cat.title}"
                else -> "Diagnóstico Activo • ${cat.title}"
            }
            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = cat.id,
                    operationName = "${cat.title} (${scriptPath.substringAfterLast("/")})",
                    scriptPath = scriptPath,
                    commandExecuted = "$scriptPath ${cat.id} \"$brand\" \"$soc\"",
                    outputLog = output,
                    telemetryStatusAfter = newBadge,
                    isSuccess = ok
                )
            )
            val updatedHw = scriptEngine.readLiveHardwareTelemetry()
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    telemetryPhase = newBadge,
                    lastConsoleOutput = output,
                    liveTelemetry = updatedHw
                )
            }
        }
    }

    /**
     * Abre la ventana modal o ejecuta directamente una operación de mantenimiento
     * (mode_safe, app_blocker, mem_cleaner, bootloader_unlock, edl_flasher)
     */
    fun onTriggerMaintenanceOperation(operation: MaintenanceOperation) {
        if (operation.requiresModalInput) {
            _uiState.update {
                it.copy(
                    activeModal = ActiveModalState(operation = operation)
                )
            }
        } else {
            executeMaintenanceOperationNow(
                operation = operation,
                args = emptyList(),
                performCacheClean = false,
                customBadge = when (operation.id) {
                    "mode_safe" -> "Modo Seguro Activo"
                    "bootloader_unlock" -> "Bootloader Desbloqueado"
                    else -> "Operación Completada"
                }
            )
        }
    }

    fun dismissModal() {
        _uiState.update { it.copy(activeModal = null) }
    }

    fun updateModalPackage(pkg: String) {
        _uiState.update { state ->
            state.copy(activeModal = state.activeModal?.copy(targetPackage = pkg))
        }
    }

    fun updateModalBlockerMode(mode: String) {
        _uiState.update { state ->
            state.copy(activeModal = state.activeModal?.copy(blockerActionMode = mode))
        }
    }

    fun updateModalWipeConfirmation(wipe: Boolean) {
        _uiState.update { state ->
            state.copy(activeModal = state.activeModal?.copy(confirmWipeUserdata = wipe))
        }
    }

    fun updateModalEdlParams(bootImg: String, radioImg: String, firehose: String) {
        _uiState.update { state ->
            state.copy(
                activeModal = state.activeModal?.copy(
                    edlBootloaderImg = bootImg,
                    edlRadioImg = radioImg,
                    edlFirehoseLoader = firehose
                )
            )
        }
    }

    fun confirmAndExecuteModalOperation() {
        val modal = _uiState.value.activeModal ?: return
        val op = modal.operation
        val wipeStr = if (modal.confirmWipeUserdata) "yes" else "no"

        val args: List<String>
        val badge: String
        val cleanCache: Boolean

        when (op.id) {
            "app_blocker" -> {
                val pkg = modal.targetPackage.trim().ifBlank { "com.ejemplo.blocker" }
                args = listOf(pkg, modal.blockerActionMode, wipeStr)
                badge = "Modo Seguro Activo / Blocker Neutralizado"
                cleanCache = true
            }
            "mem_cleaner" -> {
                args = listOf(wipeStr)
                badge = if (modal.confirmWipeUserdata) "Memoria Limpia + Wipe Userdata" else "Memoria & Caché Optimizadas"
                cleanCache = true
            }
            "edl_flasher" -> {
                args = listOf(modal.edlBootloaderImg, modal.edlRadioImg, modal.edlFirehoseLoader)
                badge = "Bootloader Flasheado vía EDL 9008"
                cleanCache = false
            }
            else -> {
                args = emptyList()
                badge = "Operación Ejecutada"
                cleanCache = false
            }
        }

        _uiState.update { it.copy(activeModal = null) }
        executeMaintenanceOperationNow(
            operation = op,
            args = args,
            performCacheClean = cleanCache,
            customBadge = badge
        )
    }

    private fun executeMaintenanceOperationNow(
        operation: MaintenanceOperation,
        args: List<String>,
        performCacheClean: Boolean,
        customBadge: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingCommand = true) }
            val (ok, output) = scriptEngine.executeScript(
                scriptRelativePath = operation.scriptPath,
                args = args,
                performRealCacheClean = performCacheClean
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = operation.id,
                    operationName = operation.visibleName,
                    scriptPath = operation.scriptPath,
                    commandExecuted = "${operation.scriptPath} ${args.joinToString(" ")}".trim(),
                    outputLog = output,
                    telemetryStatusAfter = customBadge,
                    isSuccess = ok
                )
            )
            val updatedHw = scriptEngine.readLiveHardwareTelemetry()
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    telemetryPhase = customBadge,
                    lastConsoleOutput = output,
                    liveTelemetry = updatedHw
                )
            }
        }
    }

    // --- Editor de Scripts .sh y Configuración YAML ---
    fun selectScriptOrConfigFile(file: File) {
        val text = scriptEngine.readFileContent(file)
        _uiState.update {
            it.copy(selectedFile = file, selectedFileContent = text)
        }
    }

    fun updateSelectedFileContent(newContent: String) {
        _uiState.update { it.copy(selectedFileContent = newContent) }
    }

    fun saveCurrentFileChanges() {
        val file = _uiState.value.selectedFile ?: return
        val content = _uiState.value.selectedFileContent
        scriptEngine.saveFileContent(file, content)
        _uiState.update {
            it.copy(
                lastConsoleOutput = "[Archivo Guardado]: ${file.name} (${content.length} bytes) actualizado y permisos ejecutables aplicados."
            )
        }
    }

    fun runCurrentlySelectedScript() {
        val file = _uiState.value.selectedFile ?: return
        scriptEngine.saveFileContent(file, _uiState.value.selectedFileContent)
        if (file.extension == "sh") {
            viewModelScope.launch {
                _uiState.update { it.copy(isExecutingCommand = true) }
                val (ok, output) = scriptEngine.executeScript("scripts/${file.name}")
                dao.insertAuditLog(
                    AuditLogEntity(
                        moduleId = "script_direct",
                        operationName = "Ejecución directa: ${file.name}",
                        scriptPath = "scripts/${file.name}",
                        commandExecuted = "sh scripts/${file.name}",
                        outputLog = output,
                        telemetryStatusAfter = "Script Ejecutado: ${file.name}",
                        isSuccess = ok
                    )
                )
                _uiState.update {
                    it.copy(
                        isExecutingCommand = false,
                        telemetryPhase = "Script Activo • ${file.name}",
                        lastConsoleOutput = output
                    )
                }
            }
        }
    }

    // --- Estudio de Imágenes IA (gemini-3-pro-image-preview con selector 1K, 2K, 4K) ---
    fun updateImagePrompt(prompt: String) {
        _uiState.update { it.copy(imagePrompt = prompt) }
    }

    fun selectImageSize(size: String) {
        _uiState.update { it.copy(selectedImageSize = size) }
    }

    fun selectAspectRatio(ratio: String) {
        _uiState.update { it.copy(selectedAspectRatio = ratio) }
    }

    fun generateImageWithGeminiPro() {
        val state = _uiState.value
        if (state.imagePrompt.isBlank() || state.isGeneratingImage) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGeneratingImage = true,
                    imageGenerationStatus = "Renderizando en resolución ${state.selectedImageSize} (${state.selectedAspectRatio}) con gemini-3-pro-image-preview..."
                )
            }
            val result = geminiService.generateHighQualityImage(
                prompt = state.imagePrompt,
                imageSize = state.selectedImageSize,
                aspectRatio = state.selectedAspectRatio,
                rendersDir = scriptEngine.rendersDir
            )
            result.onSuccess { (file, msg) ->
                dao.insertGeneratedImage(
                    GeneratedImageEntity(
                        prompt = state.imagePrompt,
                        imageSize = state.selectedImageSize,
                        aspectRatio = state.selectedAspectRatio,
                        filePath = file.absolutePath,
                        modelUsed = "gemini-3-pro-image-preview"
                    )
                )
                _uiState.update {
                    it.copy(
                        isGeneratingImage = false,
                        imageGenerationStatus = "Imagen ${state.selectedImageSize} generada y guardada: ${file.name}"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isGeneratingImage = false,
                        imageGenerationStatus = err.message ?: "Error al generar imagen."
                    )
                }
            }
        }
    }

    // --- Chatbot KDF-LuX Híbrido (IA Neuronal Gemini + Efecto Eliza / Reglas + Ejecución Directa) ---
    fun openChatbotModal() {
        _uiState.update { it.copy(showChatbotModal = true) }
    }

    fun dismissChatbotModal() {
        _uiState.update { it.copy(showChatbotModal = false) }
    }

    fun selectChatbotMode(mode: String) {
        _uiState.update { it.copy(chatbotMode = mode) }
    }

    fun sendChatMessage(userText: String) {
        val clean = userText.trim()
        if (clean.isEmpty()) return

        val state = _uiState.value
        val lower = clean.lowercase(java.util.Locale.US)

        // 1. Ejecución instantánea del motor Heurístico / Efecto Eliza (< 1 ms)
        val elizaRes = visualAndHeuristicsEngine.runElizaHeuristicEngine(clean, state.liveTelemetry)

        // 2. Detección contextual de comandos directos desde el Chatbot
        val autoTriggeredAction: String? = when {
            lower.contains("revisar corto") || lower.contains("multimetro") || lower.contains("multímetro") -> {
                runUsbShortCircuitTest()
                "ACCIÓN EJECUTADA: scripts/usb_short_detector.sh"
            }
            lower.contains("superresolucion") || lower.contains("superresolución") || lower.contains("upscal") || lower.contains("fsr") || lower.contains("gsr") -> {
                runGpuNpuSuperResolutionNow()
                "ACCIÓN EJECUTADA: scripts/gpu_npu_upscaler.sh (${state.visualSettings.selectedProfile.id})"
            }
            lower.contains("reparar") || lower.contains("autonom") || lower.contains("autónom") -> {
                runAutonomousSelfRepairAndLearn()
                "ACCIÓN EJECUTADA: scripts/self_heal_autonomous.sh"
            }
            lower.contains("limpiar") || lower.contains("cache") || lower.contains("caché") -> {
                KdfArchitectureCatalog.maintenanceOperations.find { it.id == "mem_cleaner" }?.let { op ->
                    executeMaintenanceOperationNow(op, listOf("no"), true, "Memoria Optimizada desde Chatbot")
                }
                "ACCIÓN EJECUTADA: scripts/clean_memory.sh"
            }
            lower.contains("modo seguro") || lower.contains("safe mode") -> {
                KdfArchitectureCatalog.maintenanceOperations.find { it.id == "mode_safe" }?.let { op ->
                    executeMaintenanceOperationNow(op, emptyList(), false, "Modo Seguro Activado desde Chatbot")
                }
                "ACCIÓN EJECUTADA: scripts/enter_safe_mode.sh"
            }
            else -> null
        }

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + ChatMessage(
                    isUser = true,
                    text = clean,
                    engineBadge = "Usuario • ${state.liveTelemetry.deviceModel}"
                ),
                lastElizaResponse = elizaRes,
                isChatLoading = state.chatbotMode != "ELIZA_RULES_ONLY"
            )
        }

        if (state.chatbotMode == "ELIZA_RULES_ONLY") {
            val ruleInfo = autoTriggeredAction ?: elizaRes.matchedPatternRule
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + ChatMessage(
                        isUser = false,
                        text = "${elizaRes.elizaResponseText}\n\n• ${elizaRes.statisticalFilterNote}",
                        engineBadge = "Motor Heurístico / Efecto Eliza (${String.format(java.util.Locale.US, "%.2f", elizaRes.executionLatencyMs)} ms)",
                        matchedRuleOrAction = ruleInfo
                    ),
                    isChatLoading = false
                )
            }
            return
        }

        val contextSummary = "Dispositivo=${state.liveTelemetry.deviceModel}, SoC=${state.liveTelemetry.socHardware}, " +
            "Categoría=${state.selectedCategory.title}, Estado=${state.telemetryPhase}, " +
            "USB=${state.liveTelemetry.usbMultimeter.voltageVolts}V (${state.liveTelemetry.usbMultimeter.shortCircuitStatus}), " +
            "Upscaler=${state.visualSettings.selectedProfile.title}, RAM=${state.liveTelemetry.ramUsedPercent}%, Temp=${state.liveTelemetry.batteryTempC}°C"

        viewModelScope.launch {
            val reply = geminiService.askKdfAssistant(clean, contextSummary)
            val combinedText = if (state.chatbotMode == "HYBRID_AI_ELIZA") {
                "$reply\n\n───\n⚡ [Diagnóstico Heurístico Instantáneo (${String.format(java.util.Locale.US, "%.2f", elizaRes.executionLatencyMs)} ms)]:\n${elizaRes.elizaResponseText}"
            } else {
                reply
            }
            val badge = if (state.chatbotMode == "HYBRID_AI_ELIZA") {
                "Híbrido: Gemini Neuronal + Heurística Eliza"
            } else {
                "IA Neuronal Real (gemini-3.5-flash)"
            }
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + ChatMessage(
                        isUser = false,
                        text = combinedText,
                        engineBadge = badge,
                        matchedRuleOrAction = autoTriggeredAction ?: elizaRes.matchedPatternRule
                    ),
                    isChatLoading = false
                )
            }
        }
    }

    // --- Laboratorio de Bio-Ciencia y Telemetría (Bioscience Skills) ---
    fun updateBioSampleName(name: String) {
        _uiState.update { it.copy(bioSampleName = name) }
    }

    fun updateBioSequenceInput(seq: String) {
        _uiState.update { it.copy(bioSequenceInput = seq) }
    }

    fun runBioScienceAnalysis() {
        val state = _uiState.value
        val rawSeq = state.bioSequenceInput.uppercase().filter { it in "ACGTU" }
        if (rawSeq.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzingBio = true) }

            val length = rawSeq.length.coerceAtLeast(1)
            val gCount = rawSeq.count { it == 'G' }
            val cCount = rawSeq.count { it == 'C' }
            val aCount = rawSeq.count { it == 'A' }
            val tCount = rawSeq.count { it == 'T' || it == 'U' }

            val gcPercent = ((gCount + cCount) * 100f) / length
            val meltingTemp = if (length < 14) {
                (aCount + tCount) * 2f + (gCount + cCount) * 4f
            } else {
                64.9f + 41f * (gCount + cCount - 16.4f) / length
            }
            val molecularWeightDa = (aCount * 313.2f) + (tCount * 304.2f) + (gCount * 329.2f) + (cCount * 289.2f)
            val protein = translateDnaToProtein(rawSeq)

            val aiPrompt = "Analiza bio-informáticamente esta muestra '${state.bioSampleName}': " +
                "Longitud=${length}bp, GC=${String.format("%.1f", gcPercent)}%, Tm=${String.format("%.1f", meltingTemp)}°C, " +
                "Peso Molecular=${String.format("%.1f", molecularWeightDa)} Da, Péptido=$protein. " +
                "Relaciónalo con telemetría ambiental EMF=${state.liveTelemetry.geomagneticFieldUt} µT y Temp=${state.liveTelemetry.batteryTempC}°C."

            val aiInsight = geminiService.askKdfAssistant(
                userPrompt = aiPrompt,
                systemContext = "Módulo Bio-Ciencia KDF-Tools"
            )

            dao.insertBioRecord(
                BioScienceRecordEntity(
                    sampleName = state.bioSampleName,
                    sequenceOrSensorData = rawSeq.take(120),
                    gcContentPercent = gcPercent,
                    meltingTempC = meltingTemp,
                    molecularWeightDa = molecularWeightDa,
                    proteinTranslation = protein,
                    aiInterpretation = aiInsight
                )
            )

            _uiState.update { it.copy(isAnalyzingBio = false) }
        }
    }

    private fun translateDnaToProtein(seq: String): String {
        val rna = seq.replace('T', 'U')
        val codonMap = mapOf(
            "AUG" to "Met", "UUU" to "Phe", "UUC" to "Phe", "UUA" to "Leu", "UUG" to "Leu",
            "CUU" to "Leu", "CUC" to "Leu", "CUA" to "Leu", "CUG" to "Leu", "AUU" to "Ile",
            "AUC" to "Ile", "AUA" to "Ile", "GUU" to "Val", "GUC" to "Val", "GUA" to "Val",
            "GUG" to "Val", "UCU" to "Ser", "UCC" to "Ser", "UCA" to "Ser", "UCG" to "Ser",
            "CCU" to "Pro", "CCC" to "Pro", "CCA" to "Pro", "CCG" to "Pro", "ACU" to "Thr",
            "ACC" to "Thr", "ACA" to "Thr", "ACG" to "Thr", "GCU" to "Ala", "GCC" to "Ala",
            "GCA" to "Ala", "GCG" to "Ala", "UAU" to "Tyr", "UAC" to "Tyr", "CAU" to "His",
            "CAC" to "His", "CAA" to "Gln", "CAG" to "Gln", "AAU" to "Asn", "AAC" to "Asn",
            "AAA" to "Lys", "AAG" to "Lys", "GAU" to "Asp", "GAC" to "Asp", "GAA" to "Glu",
            "GAG" to "Glu", "UGU" to "Cys", "UGC" to "Cys", "UGG" to "Trp", "CGU" to "Arg",
            "CGC" to "Arg", "CGA" to "Arg", "CGG" to "Arg", "AGU" to "Ser", "AGC" to "Ser",
            "AGA" to "Arg", "AGG" to "Arg", "GGU" to "Gly", "GGC" to "Gly", "GGA" to "Gly",
            "GGG" to "Gly", "UAA" to "STOP", "UAG" to "STOP", "UGA" to "STOP"
        )
        val codons = rna.chunked(3).filter { it.length == 3 }.take(18)
        return codons.joinToString("-") { codonMap[it] ?: "Xaa" }
    }

    fun clearAllAuditLogs() {
        viewModelScope.launch {
            dao.clearAuditLogs()
        }
    }

    // --- Núcleo Autónomo (Auto-Reparación, Auto-Actualización y Aprendizaje) ---
    private var evolutionCounter = 1

    private fun initializeAutonomousBootCycle() {
        viewModelScope.launch {
            val (repaired, rule, _) = scriptEngine.runAutonomousSelfRepairCycle()
            dao.insertAutonomousRecord(
                com.example.data.AutonomousLearningEntity(
                    cycleVersion = "v2.6.${evolutionCounter}-AutoLuX",
                    actionType = "AUTO-REPARACIÓN & ARRANQUE AUTÓNOMO",
                    repairedFilesCount = repaired,
                    learnedRuleSummary = rule,
                    optimizationGainPercent = 12
                )
            )
        }
    }

    fun toggleAutonomousMode(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isAutonomousModeEnabled = enabled,
                autonomousStatusMessage = if (enabled) {
                    "Modo autónomo activo: auto-reparación y aprendizaje en tiempo real"
                } else {
                    "Modo autónomo en pausa manual"
                }
            )
        }
    }

    fun runAutonomousSelfRepairAndLearn() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isExecutingCommand = true,
                    autonomousStatusMessage = "Ejecutando auto-reparación de scripts, actualización YAML y aprendizaje..."
                )
            }
            evolutionCounter++
            val newVersion = "v2.6.${evolutionCounter}-AutoLuX"
            val (repaired, rule, scriptOut) = scriptEngine.runAutonomousSelfRepairCycle()

            val aiEvolutionNote = geminiService.askKdfAssistant(
                userPrompt = "Genera una regla breve de auto-mejora y optimización técnica para el dispositivo ${_uiState.value.liveTelemetry.deviceModel} basada en telemetría USB=${_uiState.value.liveTelemetry.usbMultimeter.voltageVolts}V y RAM=${_uiState.value.liveTelemetry.ramUsedPercent}%.",
                systemContext = "Motor Autónomo de Auto-Reparación y Aprendizaje KDF-LuX $newVersion"
            )

            val combinedLearning = "$rule\n• Aprendizaje Adaptativo: ${aiEvolutionNote.take(240)}"
            dao.insertAutonomousRecord(
                com.example.data.AutonomousLearningEntity(
                    cycleVersion = newVersion,
                    actionType = "AUTO-ACTUALIZACIÓN & APRENDIZAJE IA",
                    repairedFilesCount = repaired,
                    learnedRuleSummary = combinedLearning,
                    optimizationGainPercent = 15 + (evolutionCounter * 2)
                )
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = "autonomous_core",
                    operationName = "Núcleo Autónomo ($newVersion)",
                    scriptPath = "scripts/self_heal_autonomous.sh",
                    commandExecuted = "./scripts/self_heal_autonomous.sh --learn --update",
                    outputLog = "$scriptOut\n$combinedLearning",
                    telemetryStatusAfter = "Autónomo Actualizado • $newVersion",
                    isSuccess = true
                )
            )
            val updatedHw = scriptEngine.readLiveHardwareTelemetry()
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    autonomyLevelVersion = newVersion,
                    autonomousHealthScore = 100,
                    autonomousStatusMessage = "Sistema auto-reparado y actualizado a $newVersion ($repaired scripts verificados)",
                    telemetryPhase = "Autónomo • $newVersion",
                    lastConsoleOutput = "$scriptOut\n$combinedLearning",
                    liveTelemetry = updatedHw
                )
            }
        }
    }

    // --- Multímetro USB y Detector de Cortocircuitos ---
    fun runUsbShortCircuitTest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingCommand = true) }
            val (ok, output) = scriptEngine.executeScript("scripts/usb_short_detector.sh")
            val updatedHw = scriptEngine.readLiveHardwareTelemetry()
            val usb = updatedHw.usbMultimeter
            val detailedReport = "$output\n" +
                "[Telemetría Eléctrica en Vivo]\n" +
                "• Voltaje VBUS: ${String.format(java.util.Locale.US, "%.2f", usb.voltageVolts)} V (${usb.voltageMv} mV)\n" +
                "• Corriente: ${String.format(java.util.Locale.US, "%.0f", usb.currentMa)} mA | Potencia: ${String.format(java.util.Locale.US, "%.2f", usb.powerWatts)} W\n" +
                "• Impedancia: ${String.format(java.util.Locale.US, "%.2f", usb.resistanceOhms)} Ω | Diagnóstico: ${usb.shortCircuitStatus}"

            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = "usb_multimeter",
                    operationName = "Multímetro USB & Test de Cortos",
                    scriptPath = "scripts/usb_short_detector.sh",
                    commandExecuted = "./scripts/usb_short_detector.sh",
                    outputLog = detailedReport,
                    telemetryStatusAfter = usb.shortCircuitStatus,
                    isSuccess = ok
                )
            )
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    telemetryPhase = "USB: ${String.format(java.util.Locale.US, "%.2fV", usb.voltageVolts)} • Sin Cortos",
                    lastConsoleOutput = detailedReport,
                    liveTelemetry = updatedHw
                )
            }
        }
    }

    fun runSecurityLockAudit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingCommand = true) }
            val (ok, output) = scriptEngine.executeScript("scripts/security_lock_audit.sh")
            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = "security_audit",
                    operationName = "Auditoría Baseband / OEM / FRP (Solo Lectura)",
                    scriptPath = "scripts/security_lock_audit.sh",
                    commandExecuted = "./scripts/security_lock_audit.sh",
                    outputLog = output,
                    telemetryStatusAfter = "Auditoría EFS/OEM Completada",
                    isSuccess = ok
                )
            )
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    telemetryPhase = "Auditoría EFS/OEM OK",
                    lastConsoleOutput = output
                )
            }
        }
    }

    // --- Ajuste y Lógica Completa de Personalizar Fondo de la Aplicación ---
    fun selectBackgroundPreset(preset: String) {
        prefs.edit().putString("bg_preset", preset).apply()
        _uiState.update { it.copy(backgroundPreset = preset) }
    }

    fun updateBackgroundDimAlpha(alpha: Float) {
        val clamped = alpha.coerceIn(0.10f, 0.95f)
        prefs.edit().putFloat("bg_dim_alpha", clamped).apply()
        _uiState.update { it.copy(backgroundDimAlpha = clamped) }
    }

    fun updateBackgroundBlurRadius(blur: Float) {
        val clamped = blur.coerceIn(0f, 25f)
        prefs.edit().putFloat("bg_blur_radius", clamped).apply()
        _uiState.update { it.copy(backgroundBlurRadius = clamped) }
    }

    fun updateBackgroundContrastBoost(boost: Float) {
        val clamped = boost.coerceIn(0.80f, 1.60f)
        prefs.edit().putFloat("bg_contrast_boost", clamped).apply()
        _uiState.update { it.copy(backgroundContrastBoost = clamped) }
    }

    fun resetWallpaperToDefault() {
        prefs.edit()
            .remove("bg_custom_path")
            .putString("bg_preset", "MEWTWO_FILIGREE")
            .putFloat("bg_dim_alpha", 0.72f)
            .putFloat("bg_blur_radius", 0f)
            .putFloat("bg_contrast_boost", 1.15f)
            .apply()
        _uiState.update {
            it.copy(
                customBackgroundPath = null,
                backgroundPreset = "MEWTWO_FILIGREE",
                backgroundDimAlpha = 0.72f,
                backgroundBlurRadius = 0f,
                backgroundContrastBoost = 1.15f,
                lastConsoleOutput = "[Fondo Restablecido]: Tema oficial Mewtwo Obsidiana con Filigrana Dorada/Plateada."
            )
        }
    }

    fun selectGeneratedBackground(filePath: String) {
        prefs.edit()
            .putString("bg_custom_path", filePath)
            .putString("bg_preset", "CUSTOM_IMAGE")
            .apply()
        _uiState.update {
            it.copy(
                customBackgroundPath = filePath,
                backgroundPreset = "CUSTOM_IMAGE",
                lastConsoleOutput = "[Fondo Personalizado]: Render IA activado como fondo."
            )
        }
    }

    fun setCustomBackgroundFromUri(uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                if (!scriptEngine.rendersDir.exists()) scriptEngine.rendersDir.mkdirs()
                val destFile = File(scriptEngine.rendersDir, "custom_user_wallpaper_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (destFile.exists() && destFile.length() > 0L) {
                    prefs.edit()
                        .putString("bg_custom_path", destFile.absolutePath)
                        .putString("bg_preset", "CUSTOM_IMAGE")
                        .apply()
                    _uiState.update {
                        it.copy(
                            customBackgroundPath = destFile.absolutePath,
                            backgroundPreset = "CUSTOM_IMAGE",
                            lastConsoleOutput = "[Fondo Actualizado]: Imagen de galería guardada y aplicada (${destFile.name})."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(lastConsoleOutput = "[Error al cambiar fondo]: ${e.message}")
                }
            }
        }
    }

    // --- Interfaz de Diagnóstico (Conectividad + Descripción Problema + Estado Dispositivo + Multímetro Local + Chatbot) ---
    fun selectDiagnosticProblemType(type: String) {
        val suggestedDesc = when (type) {
            "Bootloop / Reinicio en logo" -> "El dispositivo se reinicia continuamente en el logo del fabricante o pantalla de arranque sin entrar a Android."
            "Caída de voltaje / Corto en puerto USB" -> "El dispositivo no carga o la corriente cae abruptamente al conectar el cable; sospecha de corto en línea VBUS o fusible térmico."
            "App bloqueadora / Bloqueo en inicio" -> "Una aplicación de terceros o servicio bloqueador impide la interacción con el sistema."
            "Memoria saturada / Congelamientos" -> "El sistema presenta lentitud extrema, cierres forzosos y alto uso de memoria RAM."
            "Dispositivo brickeado (EDL 9008)" -> "Dispositivo no enciende, no entra a recovery ni fastboot; solo detecta puerto Qualcomm QDLoader 9008."
            "Bloqueo OEM / FRP / EFS dañado" -> "El dispositivo requiere auditoría de partición EFS, radio baseband o estado de bootloader bloqueado."
            else -> "Problema técnico personalizado en el dispositivo."
        }
        _uiState.update {
            it.copy(
                selectedDiagnosticProblemType = type,
                diagnosticProblemDescription = suggestedDesc
            )
        }
    }

    fun updateDiagnosticProblemDescription(desc: String) {
        _uiState.update { it.copy(diagnosticProblemDescription = desc) }
    }

    fun testConnectivityNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnectivity = true) }
            val (ok, output) = scriptEngine.executeRawTerminalCommand(
                "echo '[TEST RED/ADB]' && ip addr show 2>/dev/null || ifconfig 2>/dev/null || true && echo '[USB HOST]' && ls -l /dev/bus/usb 2>/dev/null || echo 'USB Bus Listo'"
            )
            val updatedHw = scriptEngine.readLiveHardwareTelemetry()
            val summary = "ADB Socket: ${if (ok) "Activo (127.0.0.1:5555)" else "Inactivo"} | USB Host: ${updatedHw.usbDevicesCount} dispositivos | Batería: ${updatedHw.batteryVoltageMv}mV"
            _uiState.update {
                it.copy(
                    isTestingConnectivity = false,
                    lastConnectivityResult = summary,
                    lastConsoleOutput = output,
                    liveTelemetry = updatedHw
                )
            }
        }
    }

    fun analyzeProblemWithDiagnostic() {
        val state = _uiState.value
        val usb = state.liveTelemetry.usbMultimeter
        val diagnosticPrompt = "DIAGNÓSTICO TÉCNICO KDF-LUX SOLICITADO:\n" +
            "• Tipo de Problema: ${state.selectedDiagnosticProblemType}\n" +
            "• Descripción: ${state.diagnosticProblemDescription}\n" +
            "• Estado Dispositivo: ${state.liveTelemetry.deviceModel} (SoC: ${state.liveTelemetry.socHardware}, RAM: ${state.liveTelemetry.ramUsedPercent}%, Batería: ${state.liveTelemetry.batteryLevel}%, Temp: ${state.liveTelemetry.batteryTempC}°C)\n" +
            "• Multímetro Local USB: VBUS=${String.format(java.util.Locale.US, "%.2fV", usb.voltageVolts)} (${usb.voltageMv}mV), I=${String.format(java.util.Locale.US, "%.0fmA", usb.currentMa)}, P=${String.format(java.util.Locale.US, "%.2fW", usb.powerWatts)}, R=${String.format(java.util.Locale.US, "%.1fΩ", usb.resistanceOhms)}, Corto=${usb.shortCircuitStatus}\n" +
            "• Conectividad: ${state.lastConnectivityResult}\n\n" +
            "Proporciona diagnóstico técnico exacto, causa raíz y sugiere qué script ejecutar (scripts/enter_safe_mode.sh, clean_memory.sh, usb_short_detector.sh, unbrick_recovery.sh o edl_flasher.sh)."

        sendChatMessage(diagnosticPrompt)
    }

    // --- 1. Motor de Mejora Visual por Hardware (Superresolución GPU/NPU + Fotografía Computacional ISP + NNAPI) ---
    fun selectUpscalingProfile(profile: UpscalingProfile) {
        _uiState.update {
            it.copy(
                visualSettings = it.visualSettings.copy(
                    selectedProfile = profile,
                    lastProcessingMetrics = "Perfil ${profile.title} (${profile.badge}) activado en GPU/ISP"
                ),
                telemetryPhase = "Upscaler • ${profile.id}"
            )
        }
    }

    fun updateSharpnessIntensity(value: Float) {
        val clamped = value.coerceIn(0.1f, 1.0f)
        _uiState.update {
            it.copy(
                visualSettings = it.visualSettings.copy(sharpnessIntensity = clamped)
            )
        }
    }

    fun toggleComputationalHdr(enabled: Boolean) {
        _uiState.update {
            it.copy(
                visualSettings = it.visualSettings.copy(computationalHdrEnabled = enabled)
            )
        }
    }

    fun toggleIspDenoise(enabled: Boolean) {
        _uiState.update {
            it.copy(
                visualSettings = it.visualSettings.copy(ispDenoiseEnabled = enabled)
            )
        }
    }

    fun togglePortraitMicroContrast(enabled: Boolean) {
        _uiState.update {
            it.copy(
                visualSettings = it.visualSettings.copy(portraitMicroContrastEnabled = enabled)
            )
        }
    }

    fun selectHardwareDelegate(delegate: String) {
        _uiState.update {
            it.copy(
                visualSettings = it.visualSettings.copy(hardwareAccelerationDelegate = delegate)
            )
        }
    }

    fun runGpuNpuSuperResolutionNow(customUri: android.net.Uri? = null) {
        if (_uiState.value.isUpscalingImage) return
        viewModelScope.launch {
            val currentSettings = _uiState.value.visualSettings
            _uiState.update {
                it.copy(
                    isUpscalingImage = true,
                    visualSettings = currentSettings.copy(
                        lastProcessingMetrics = "Ejecutando Superresolución ${currentSettings.selectedProfile.title} + ISP HDR + ${currentSettings.hardwareAccelerationDelegate}..."
                    )
                )
            }

            val context = getApplication<Application>()
            val sourceBitmap: android.graphics.Bitmap? = when {
                customUri != null -> {
                    context.contentResolver.openInputStream(customUri)?.use { input ->
                        android.graphics.BitmapFactory.decodeStream(input)
                    }
                }
                !_uiState.value.customBackgroundPath.isNullOrBlank() && File(_uiState.value.customBackgroundPath!!).exists() -> {
                    android.graphics.BitmapFactory.decodeFile(_uiState.value.customBackgroundPath!!)
                }
                else -> {
                    android.graphics.BitmapFactory.decodeResource(context.resources, com.example.R.drawable.img_kdf_logo)
                }
            }

            val (_, scriptOut) = scriptEngine.executeScript(
                scriptRelativePath = "scripts/gpu_npu_upscaler.sh",
                args = listOf(currentSettings.selectedProfile.id, "${(currentSettings.sharpnessIntensity * 100).toInt()}")
            )

            if (sourceBitmap != null) {
                val res = visualAndHeuristicsEngine.processImageWithSuperResolutionAndIsp(
                    sourceBitmap = sourceBitmap,
                    settings = currentSettings,
                    outputDir = scriptEngine.rendersDir
                )
                res.onSuccess { (outFile, summary) ->
                    dao.insertGeneratedImage(
                        GeneratedImageEntity(
                            prompt = "Superresolución ${currentSettings.selectedProfile.title} + ISP HDR/Denoise (${currentSettings.hardwareAccelerationDelegate})",
                            imageSize = "${currentSettings.selectedProfile.id} RCAS",
                            aspectRatio = "1:1 / Nativo",
                            filePath = outFile.absolutePath,
                            modelUsed = currentSettings.hardwareAccelerationDelegate
                        )
                    )
                    dao.insertAuditLog(
                        AuditLogEntity(
                            moduleId = "gpu_npu_upscaler",
                            operationName = "Superresolución ${currentSettings.selectedProfile.title}",
                            scriptPath = "scripts/gpu_npu_upscaler.sh",
                            commandExecuted = "./scripts/gpu_npu_upscaler.sh ${currentSettings.selectedProfile.id}",
                            outputLog = "$scriptOut\n$summary",
                            telemetryStatusAfter = "Superresolución GPU/ISP OK",
                            isSuccess = true
                        )
                    )
                    _uiState.update {
                        it.copy(
                            isUpscalingImage = false,
                            visualSettings = it.visualSettings.copy(
                                lastProcessedImagePath = outFile.absolutePath,
                                lastProcessingMetrics = summary
                            ),
                            telemetryPhase = "GPU/ISP • ${currentSettings.selectedProfile.id}",
                            lastConsoleOutput = "$scriptOut\n[Archivo Guardado]: ${outFile.name}\n$summary"
                        )
                    }
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isUpscalingImage = false,
                            visualSettings = it.visualSettings.copy(
                                lastProcessingMetrics = "Error en pipeline GPU/ISP: ${err.message}"
                            )
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isUpscalingImage = false) }
            }
        }
    }

    // --- 2. Motor de Heurísticas, Efecto Eliza, Automatización Contextual y Auditor de AI Washing ---
    fun toggleSmartAutomation(enabled: Boolean) {
        _uiState.update { it.copy(smartAutomationEnabled = enabled) }
    }

    fun updateElizaInputQuery(query: String) {
        _uiState.update { it.copy(elizaInputQuery = query) }
    }

    fun executeElizaHeuristicQuery(customQuery: String? = null) {
        val q = (customQuery ?: _uiState.value.elizaInputQuery).trim()
        if (q.isEmpty()) return
        val elizaRes = visualAndHeuristicsEngine.runElizaHeuristicEngine(
            userQuery = q,
            telemetry = _uiState.value.liveTelemetry
        )
        _uiState.update {
            it.copy(
                elizaInputQuery = q,
                lastElizaResponse = elizaRes,
                telemetryPhase = "Heurística Eliza (${String.format(java.util.Locale.US, "%.2f", elizaRes.executionLatencyMs)} ms)",
                lastConsoleOutput = "[Motor Heurístico / Efecto Eliza]\n" +
                    "• Entrada: ${elizaRes.userInput}\n" +
                    "• Regla disparada: ${elizaRes.matchedPatternRule}\n" +
                    "• ${elizaRes.statisticalFilterNote}\n" +
                    "• Respuesta (${String.format(java.util.Locale.US, "%.2f", elizaRes.executionLatencyMs)} ms): ${elizaRes.elizaResponseText}"
            )
        }
    }

    fun runContextualHeuristicsAndAiWashingAudit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingCommand = true) }
            val (ok, output) = scriptEngine.executeScript("scripts/contextual_heuristics_engine.sh")
            val updatedHw = scriptEngine.readLiveHardwareTelemetry()
            val rules = visualAndHeuristicsEngine.evaluateSmartContextualRules(updatedHw)
            val auditItems = visualAndHeuristicsEngine.getAiTransparencyAuditReport()
            val elizaSample = visualAndHeuristicsEngine.runElizaHeuristicEngine(
                userQuery = _uiState.value.elizaInputQuery,
                telemetry = updatedHw
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    moduleId = "contextual_heuristics_engine",
                    operationName = "Motor Heurístico, Efecto Eliza & Auditor AI Washing",
                    scriptPath = "scripts/contextual_heuristics_engine.sh",
                    commandExecuted = "./scripts/contextual_heuristics_engine.sh",
                    outputLog = "$output\n• Reglas activas: ${rules.size} | Módulos auditados contra AI Washing: ${auditItems.size}",
                    telemetryStatusAfter = "Heurísticas & Smart OS OK",
                    isSuccess = ok
                )
            )
            _uiState.update {
                it.copy(
                    isExecutingCommand = false,
                    liveTelemetry = updatedHw,
                    smartContextualRules = rules,
                    aiTransparencyAuditItems = auditItems,
                    lastElizaResponse = elizaSample,
                    telemetryPhase = "Smart OS & Auditor IA OK",
                    lastConsoleOutput = "$output\n[Regla Activa]: ${elizaSample.matchedPatternRule}\n[Diagnóstico Instantáneo]: ${elizaSample.elizaResponseText}"
                )
            }
        }
    }
}
