package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KdfScreenTab
import com.example.ui.KdfToolsViewModel
import com.example.ui.components.BimetallicPhysicalSwitch
import com.example.ui.components.DamaskFiligreeBackground
import com.example.ui.components.KdfLogoShowcaseModal
import com.example.ui.components.MetallicGradientTitle
import com.example.ui.components.SwissChronographTelemetryHeader
import com.example.ui.screens.AiStudioAndConsoleScreen
import com.example.ui.screens.DeviceModulesDashboardSection
import com.example.ui.screens.DiagnosticCenterScreen
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SmartToy
import com.example.ui.screens.HardwarePermissionManagerCard
import com.example.ui.screens.PersonalAssistantAndPlannerScreen
import com.example.ui.screens.SystemBrowserScreen
import com.example.ui.screens.SystemMapsScreen
import com.example.ui.screens.HardwareVisualSuperResolutionCard
import com.example.ui.screens.HeuristicsElizaAndAiWashingCard
import com.example.ui.screens.KdfBimetallicChatbotModal
import com.example.ui.screens.LiveConsoleOutputCard
import com.example.ui.screens.MaintenanceOperationModal
import com.example.ui.screens.MaintenanceOperationsSection
import com.example.ui.screens.ScriptsAndYamlEditorScreen
import com.example.ui.screens.WallpaperCustomizerScreen
import com.example.ui.theme.ChromeSilverMetallicBrush
import com.example.ui.theme.DeepGoldShadow
import com.example.ui.theme.DividerBimetallicBrush
import com.example.ui.theme.GoldMetallicBrush
import com.example.ui.theme.GraphiteCharcoalText
import com.example.ui.theme.HighGlossGoldHighlight
import com.example.ui.theme.ImperialGoldPrimary
import com.example.ui.theme.LeadGreySecondaryText
import com.example.ui.theme.LiquidSilverLight
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoRoyalIndigo
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.PureCeramicWhite
import com.example.ui.theme.SpecularWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val kdfViewModel: KdfToolsViewModel = viewModel()
            val uiState by kdfViewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.isObsidianMode) {
                KdfToolsRootApp(viewModel = kdfViewModel)
            }
        }
    }
}

@Composable
fun KdfToolsRootApp(viewModel: KdfToolsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val generatedImages by viewModel.generatedImages.collectAsStateWithLifecycle()

    if (uiState.currentTab != KdfScreenTab.DASHBOARD_MODULES) {
        BackHandler {
            viewModel.selectTab(KdfScreenTab.DASHBOARD_MODULES)
        }
    }

    val liveContrastBoost = uiState.visualSettings.selectedProfile.contrastBoost *
        (0.85f + uiState.visualSettings.sharpnessIntensity * 0.30f) *
        (uiState.backgroundContrastBoost / 1.15f)

    DamaskFiligreeBackground(
        isObsidianMode = uiState.isObsidianMode,
        customBackgroundPath = uiState.customBackgroundPath,
        backgroundPreset = uiState.backgroundPreset,
        backgroundDimAlpha = uiState.backgroundDimAlpha,
        backgroundBlurRadius = uiState.backgroundBlurRadius,
        visualContrastBoost = liveContrastBoost,
        computationalHdrActive = uiState.visualSettings.computationalHdrEnabled
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                CompactBimetallicTopBar(
                    deviceModel = uiState.liveTelemetry.deviceModel,
                    usbStatus = "${String.format(java.util.Locale.US, "%.2fV", uiState.liveTelemetry.usbMultimeter.voltageVolts)} • ${uiState.autonomyLevelVersion}",
                    isObsidianMode = uiState.isObsidianMode,
                    onLogoClick = { viewModel.openLogoModal() },
                    onOpenChatbot = { viewModel.openChatbotModal() },
                    onToggleTheme = { viewModel.toggleObsidianMode(it) }
                )
            },
            bottomBar = {
                CompactBimetallicNavigationBar(
                    currentTab = uiState.currentTab,
                    isObsidianMode = uiState.isObsidianMode,
                    onSelectTab = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 720.dp)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Widget Central de Alta Relojería Suiza con Logo KDF en PNG recortado y Telemetría
                    item {
                        SwissChronographTelemetryHeader(
                            telemetryPhase = uiState.telemetryPhase,
                            ramUsedPercent = uiState.liveTelemetry.ramUsedPercent,
                            ramSummary = "${uiState.liveTelemetry.ramAvailableMb}/${uiState.liveTelemetry.ramTotalMb}MB",
                            storageFreeGb = uiState.liveTelemetry.storageFreeGb,
                            batteryTempC = uiState.liveTelemetry.batteryTempC,
                            activeCategoryName = uiState.selectedCategory.title,
                            isObsidianMode = uiState.isObsidianMode,
                            onLogoClick = { viewModel.openLogoModal() }
                        )
                    }

                    // Contenido dinámico según pestaña activa (sin botones ni tarjetas duplicadas)
                    item {
                        when (uiState.currentTab) {
                            KdfScreenTab.DASHBOARD_MODULES -> {
                                DeviceModulesDashboardSection(
                                    uiState = uiState,
                                    onSelectCategory = viewModel::selectCategoryModule,
                                    onSelectBrand = viewModel::selectBrand,
                                    onSelectProcessor = viewModel::selectProcessor,
                                    onExecuteScript = viewModel::executeDeviceModuleScript,
                                    onTriggerOperation = viewModel::onTriggerMaintenanceOperation,
                                    onUpdateAdbEndpoint = viewModel::updateWirelessAdbEndpoint,
                                    onVerifyPermissions = viewModel::verifyHardwarePermissions,
                                    onUpdateTerminalInput = viewModel::updateTerminalCommandInput,
                                    onExecuteTerminalCmd = viewModel::runRawTerminalCommand
                                )
                            }

                            KdfScreenTab.DIAGNOSTIC_CENTER -> {
                                DiagnosticCenterScreen(
                                    uiState = uiState,
                                    onTestConnectivity = viewModel::testConnectivityNow,
                                    onSelectProblemType = viewModel::selectDiagnosticProblemType,
                                    onUpdateProblemDescription = viewModel::updateDiagnosticProblemDescription,
                                    onAnalyzeProblem = viewModel::analyzeProblemWithDiagnostic,
                                    onRunUsbShortTest = viewModel::runUsbShortCircuitTest,
                                    onSelectChatbotMode = viewModel::selectChatbotMode,
                                    onSendChatMessage = viewModel::sendChatMessage,
                                    onOpenFullChatModal = viewModel::openChatbotModal
                                )
                            }

                            KdfScreenTab.MAINTENANCE_OPS -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    MaintenanceOperationsSection(
                                        uiState = uiState,
                                        onTriggerOperation = viewModel::onTriggerMaintenanceOperation
                                    )
                                    HardwarePermissionManagerCard(
                                        uiState = uiState,
                                        onUpdateAdbEndpoint = viewModel::updateWirelessAdbEndpoint,
                                        onVerifyPermissions = viewModel::verifyHardwarePermissions
                                    )
                                    LiveConsoleOutputCard(
                                        consoleOutput = uiState.lastConsoleOutput,
                                        isExecuting = uiState.isExecutingCommand,
                                        isObsidianMode = uiState.isObsidianMode,
                                        terminalInput = uiState.terminalCommandInput,
                                        onUpdateTerminalInput = viewModel::updateTerminalCommandInput,
                                        onExecuteTerminalCmd = viewModel::runRawTerminalCommand
                                    )
                                }
                            }

                            KdfScreenTab.SYSTEM_BROWSER -> {
                                SystemBrowserScreen(
                                    isObsidianMode = uiState.isObsidianMode
                                )
                            }

                            KdfScreenTab.SYSTEM_MAPS -> {
                                SystemMapsScreen(
                                    isObsidianMode = uiState.isObsidianMode,
                                    onSendTelemetryToOrchestrator = { coords ->
                                        viewModel.sendChatMessage("Telemetría de posición enviada al Orquestador: $coords")
                                    }
                                )
                            }

                            KdfScreenTab.PERSONAL_ASSISTANT_PLANNER -> {
                                PersonalAssistantAndPlannerScreen(
                                    isObsidianMode = uiState.isObsidianMode,
                                    onExecuteStep = { cmd -> viewModel.runRawTerminalCommand(cmd) },
                                    onConsultAi = viewModel::sendChatMessage
                                )
                            }

                            KdfScreenTab.HARDWARE_VISUAL -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    HardwareVisualSuperResolutionCard(
                                        uiState = uiState,
                                        onSelectUpscalingProfile = viewModel::selectUpscalingProfile,
                                        onUpdateSharpness = viewModel::updateSharpnessIntensity,
                                        onToggleHdr = viewModel::toggleComputationalHdr,
                                        onToggleDenoise = viewModel::toggleIspDenoise,
                                        onToggleMicroContrast = viewModel::togglePortraitMicroContrast,
                                        onSelectHardwareDelegate = viewModel::selectHardwareDelegate,
                                        onRunSuperResolution = viewModel::runGpuNpuSuperResolutionNow,
                                        onApplyProcessedAsWallpaper = viewModel::selectGeneratedBackground
                                    )
                                    HeuristicsElizaAndAiWashingCard(
                                        uiState = uiState,
                                        onToggleSmartAutomation = viewModel::toggleSmartAutomation,
                                        onUpdateElizaQuery = viewModel::updateElizaInputQuery,
                                        onExecuteElizaQuery = viewModel::executeElizaHeuristicQuery,
                                        onRunFullHeuristicsAudit = viewModel::runContextualHeuristicsAndAiWashingAudit
                                    )
                                }
                            }

                            KdfScreenTab.WALLPAPER_CUSTOMIZER -> {
                                WallpaperCustomizerScreen(
                                    uiState = uiState,
                                    generatedImages = generatedImages,
                                    onSelectPreset = viewModel::selectBackgroundPreset,
                                    onPickGalleryUri = viewModel::setCustomBackgroundFromUri,
                                    onSelectGeneratedImage = viewModel::selectGeneratedBackground,
                                    onUpdateDimAlpha = viewModel::updateBackgroundDimAlpha,
                                    onUpdateBlurRadius = viewModel::updateBackgroundBlurRadius,
                                    onUpdateContrastBoost = viewModel::updateBackgroundContrastBoost,
                                    onResetToDefault = viewModel::resetWallpaperToDefault
                                )
                            }

                            KdfScreenTab.SCRIPTS_STUDIO -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    ScriptsAndYamlEditorScreen(
                                        uiState = uiState,
                                        onSelectFile = viewModel::selectScriptOrConfigFile,
                                        onUpdateContent = viewModel::updateSelectedFileContent,
                                        onSaveFile = viewModel::saveCurrentFileChanges,
                                        onRunScript = viewModel::runCurrentlySelectedScript
                                    )
                                    AiStudioAndConsoleScreen(
                                        uiState = uiState,
                                        generatedImages = generatedImages,
                                        onUpdatePrompt = viewModel::updateImagePrompt,
                                        onSelectImageSize = viewModel::selectImageSize,
                                        onSelectAspectRatio = viewModel::selectAspectRatio,
                                        onGenerateImage = viewModel::generateImageWithGeminiPro,
                                        onSendChatMessage = viewModel::sendChatMessage
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ventana Modal Reutilizada para App Blocker, Memory Cleaner y EDL Flasher
        uiState.activeModal?.let { modalState ->
            MaintenanceOperationModal(
                modalState = modalState,
                installedPackages = uiState.installedPackages,
                isObsidianMode = uiState.isObsidianMode,
                onDismiss = viewModel::dismissModal,
                onUpdatePackage = viewModel::updateModalPackage,
                onUpdateBlockerMode = viewModel::updateModalBlockerMode,
                onUpdateWipeConfirm = viewModel::updateModalWipeConfirmation,
                onUpdateEdlParams = viewModel::updateModalEdlParams,
                onConfirmExecute = viewModel::confirmAndExecuteModalOperation
            )
        }

        // Ventana Modal del Logo KDF-Tools (PNG Recortado + Paleta Oscura Mewtwo)
        if (uiState.showLogoModal) {
            KdfLogoShowcaseModal(
                exportStatus = uiState.logoExportStatus,
                onExportPngCopy = viewModel::exportCroppedLogoPng,
                onDismiss = viewModel::dismissLogoModal
            )
        }

        // Ventana Modal del Chatbot KDF-LuX (Gemini IA + Efecto Eliza + Comandos Directos)
        if (uiState.showChatbotModal) {
            KdfBimetallicChatbotModal(
                uiState = uiState,
                onSelectMode = viewModel::selectChatbotMode,
                onSendMessage = viewModel::sendChatMessage,
                onDismiss = viewModel::dismissChatbotModal
            )
        }
    }
}

@Composable
private fun CompactBimetallicTopBar(
    deviceModel: String,
    usbStatus: String,
    isObsidianMode: Boolean,
    onLogoClick: () -> Unit,
    onOpenChatbot: () -> Unit,
    onToggleTheme: (Boolean) -> Unit
) {
    val barBg = if (isObsidianMode) {
        Brush.verticalGradient(listOf(MewtwoObsidianBlack, Color(0xFF171226)))
    } else {
        Brush.verticalGradient(listOf(PureCeramicWhite, PearlIvoryCanvas))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onLogoClick)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_kdf_logo),
                    contentDescription = "Logo KDF-Tools PNG Recortado",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                Column {
                    MetallicGradientTitle(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 15
                    )
                    Text(
                        text = "$deviceModel • $usbStatus",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isObsidianMode) LiquidSilverLight else LeadGreySecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldMetallicBrush)
                        .padding(1.dp)
                        .background(MewtwoObsidianBlack, RoundedCornerShape(9.dp))
                        .clickable(onClick = onOpenChatbot)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("top_bar_chatbot_button")
                ) {
                    Text(
                        text = "Chat IA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = HighGlossGoldHighlight
                    )
                }
                Text(
                    text = if (isObsidianMode) "Mewtwo Noir" else "Porcelana",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isObsidianMode) HighGlossGoldHighlight else ImperialGoldPrimary
                )
                BimetallicPhysicalSwitch(
                    checked = isObsidianMode,
                    onCheckedChange = onToggleTheme,
                    testTag = "theme_mode_switch"
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(DividerBimetallicBrush)
        )
    }
}

@Composable
private fun CompactBimetallicNavigationBar(
    currentTab: KdfScreenTab,
    isObsidianMode: Boolean,
    onSelectTab: (KdfScreenTab) -> Unit
) {
    val navBg = if (isObsidianMode) {
        Brush.verticalGradient(listOf(Color(0xFF171226), MewtwoObsidianBlack))
    } else {
        Brush.verticalGradient(listOf(PureCeramicWhite, PearlIvoryCanvas))
    }

    val items = listOf(
        Triple(KdfScreenTab.DASHBOARD_MODULES, stringResource(id = R.string.tab_dashboard), Icons.Filled.Dashboard),
        Triple(KdfScreenTab.DIAGNOSTIC_CENTER, stringResource(id = R.string.tab_diagnostic), Icons.Filled.PermDeviceInformation),
        Triple(KdfScreenTab.MAINTENANCE_OPS, stringResource(id = R.string.tab_maintenance), Icons.Filled.Build),
        Triple(KdfScreenTab.SYSTEM_BROWSER, "Navegador", Icons.Filled.Language),
        Triple(KdfScreenTab.SYSTEM_MAPS, "Mapas", Icons.Filled.Map),
        Triple(KdfScreenTab.PERSONAL_ASSISTANT_PLANNER, "Asistente", Icons.Filled.SmartToy),
        Triple(KdfScreenTab.HARDWARE_VISUAL, stringResource(id = R.string.tab_hardware_visual), Icons.Filled.HighQuality),
        Triple(KdfScreenTab.WALLPAPER_CUSTOMIZER, stringResource(id = R.string.tab_wallpaper), Icons.Filled.Palette),
        Triple(KdfScreenTab.SCRIPTS_STUDIO, stringResource(id = R.string.tab_scripts_studio), Icons.Filled.Code)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .background(navBg)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(DividerBimetallicBrush)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (tab, label, icon) ->
                val active = currentTab == tab
                CompactNavTabItem(
                    label = label,
                    icon = icon,
                    active = active,
                    isObsidianMode = isObsidianMode,
                    onClick = { onSelectTab(tab) },
                    testTag = "nav_tab_${tab.name.lowercase()}"
                )
            }
        }
    }
}

@Composable
private fun CompactNavTabItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    isObsidianMode: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val borderBrush = if (active) GoldMetallicBrush else ChromeSilverMetallicBrush
    val bgBrush = when {
        active && isObsidianMode -> Brush.verticalGradient(
            listOf(MewtwoRoyalIndigo, MewtwoObsidianBlack)
        )
        active -> Brush.verticalGradient(
            listOf(Color(0xFFFFFAEB), PureCeramicWhite)
        )
        else -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (active) {
                    Modifier
                        .background(borderBrush)
                        .padding(1.2.dp)
                        .background(bgBrush, RoundedCornerShape(9.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 5.dp, vertical = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = when {
                    active -> HighGlossGoldHighlight
                    isObsidianMode -> LiquidSilverLight.copy(alpha = 0.78f)
                    else -> LeadGreySecondaryText
                },
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                ),
                color = when {
                    active -> if (isObsidianMode) SpecularWhite else DeepGoldShadow
                    isObsidianMode -> LiquidSilverLight.copy(alpha = 0.78f)
                    else -> GraphiteCharcoalText
                },
                maxLines = 1
            )
        }
    }
}
