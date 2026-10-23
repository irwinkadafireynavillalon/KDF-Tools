package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.ActiveModalState
import com.example.data.DeviceCategoryModule
import com.example.data.InstalledAppInfo
import com.example.data.KdfArchitectureCatalog
import com.example.data.MaintenanceOperation
import com.example.ui.KdfUiState
import com.example.ui.components.Bimetallic3DIcon
import com.example.ui.components.BimetallicCeramicCard
import com.example.ui.components.BimetallicDivider
import com.example.ui.components.BimetallicPhysicalSwitch
import com.example.ui.components.JewelActionButton
import com.example.ui.components.MetallicGradientTitle
import com.example.ui.theme.ChromeSilverMetallicBrush
import com.example.ui.theme.DeepGoldShadow
import com.example.ui.theme.GoldMetallicBrush
import com.example.ui.theme.GraphiteCharcoalText
import com.example.ui.theme.HighGlossGoldHighlight
import com.example.ui.theme.ImperialGoldPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LeadGreySecondaryText
import com.example.ui.theme.LiquidSilverLight
import com.example.ui.theme.MewtwoDeepViolet
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.PureCeramicWhite
import com.example.ui.theme.SpecularWhite
import com.example.ui.theme.TelemetryCriticalCrimson

fun iconForDeviceCategory(id: String): ImageVector = when (id) {
    "smartphones" -> Icons.Filled.PhoneAndroid
    "tablets" -> Icons.Filled.TabletMac
    "laptops" -> Icons.Filled.Computer
    "smart_tv" -> Icons.Filled.Tv
    "wearables" -> Icons.Filled.Watch
    "automotriz" -> Icons.Filled.DirectionsCar
    else -> Icons.Filled.PhoneAndroid
}

fun iconForMaintenanceOp(id: String): ImageVector = when (id) {
    "mode_safe" -> Icons.Filled.Shield
    "app_blocker" -> Icons.Filled.Security
    "mem_cleaner" -> Icons.Filled.CleaningServices
    "bootloader_unlock" -> Icons.Filled.LockOpen
    "edl_flasher" -> Icons.Filled.FlashOn
    else -> Icons.Filled.Build
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceModulesDashboardSection(
    uiState: KdfUiState,
    onSelectCategory: (DeviceCategoryModule) -> Unit,
    onSelectBrand: (String) -> Unit,
    onSelectProcessor: (String) -> Unit,
    onExecuteScript: (String) -> Unit,
    onTriggerOperation: (MaintenanceOperation) -> Unit,
    onUpdateAdbEndpoint: (String) -> Unit = {},
    onVerifyPermissions: () -> Unit = {},
    onUpdateTerminalInput: (String) -> Unit = {},
    onExecuteTerminalCmd: (String?) -> Unit = {}
) {
    val isDark = uiState.isObsidianMode

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Barra de Control Rápido: Sidebar "Módulos por dispositivo" + Botón "./run.sh (Reiniciar UI)"
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            accentMewtwo = true,
            outerGold = true,
            contentPadding = PaddingValues(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    MetallicGradientTitle(
                        text = "SIDEBAR KDF-LUX & RECARGA ./RUN.SH",
                        fontSize = 13
                    )
                    Text(
                        text = "6 categorías en barra lateral + 🛠 Herramientas de mantenimiento justo debajo.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                    )
                }
                JewelActionButton(
                    text = "./run.sh (Recargar UI)",
                    icon = Icons.Filled.PlayArrow,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = { onExecuteTerminalCmd("./run.sh") },
                    testTag = "btn_run_sh_reload"
                )
            }
        }

        // 1. Cuadrícula / Sidebar de 6 Iconos de Acceso Directo (Device Management - Módulos por Dispositivo)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = true,
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetallicGradientTitle(
                    text = "SIDEBAR → MÓDULOS POR DISPOSITIVO (6 CATEGORÍAS)",
                    fontSize = 13
                )
                Text(
                    text = "adb / fastboot",
                    style = MaterialTheme.typography.labelSmall,
                    color = MewtwoPsychicPurple
                )
            }
            BimetallicDivider()

            // Cuadrícula 3x2 de los 6 módulos con iconos 3D de doble aro metálico
            val modules = KdfArchitectureCatalog.deviceModules
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                modules.chunked(3).forEach { rowModules ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowModules.forEach { module ->
                            val isSelected = uiState.selectedCategory.id == module.id
                            DeviceCategoryGridItem(
                                module = module,
                                isSelected = isSelected,
                                isObsidianMode = isDark,
                                onClick = { onSelectCategory(module) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Panel de Configuración Activa de la Categoría Seleccionada (Marcas, Procesadores y Scripts)
        val activeModule = uiState.selectedCategory
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            accentMewtwo = true,
            outerGold = false,
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    MetallicGradientTitle(
                        text = "MATRIZ ACTIVA: ${activeModule.title.uppercase()}",
                        fontSize = 14
                    )
                    Text(
                        text = activeModule.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ruta Config: ${activeModule.configPath} • ${activeModule.connectionProtocol}",
                style = MaterialTheme.typography.labelSmall,
                color = ImperialGoldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Marcas Soportadas (brands):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) SpecularWhite else GraphiteCharcoalText
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeModule.supportedBrands.forEach { brand ->
                    val selected = uiState.selectedBrand == brand
                    MetallicChipPill(
                        label = brand,
                        selected = selected,
                        isObsidianMode = isDark,
                        onClick = { onSelectBrand(brand) }
                    )
                }
            }

            Text(
                text = "Arquitectura SoC / Procesadores (processors):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) SpecularWhite else GraphiteCharcoalText
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeModule.supportedProcessors.forEach { proc ->
                    val selected = uiState.selectedProcessor == proc
                    MetallicChipPill(
                        label = proc,
                        selected = selected,
                        isObsidianMode = isDark,
                        onClick = { onSelectProcessor(proc) }
                    )
                }
            }

            BimetallicDivider()
            Text(
                text = "Scripts del Módulo de Dispositivo (Ejecución Directa):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) HighGlossGoldHighlight else GraphiteCharcoalText
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeModule.primaryScripts.forEach { scriptPath ->
                    JewelActionButton(
                        text = scriptPath.substringAfterLast("/"),
                        icon = Icons.Filled.PlayArrow,
                        isPrimaryGold = true,
                        isObsidianMode = isDark,
                        onClick = { onExecuteScript(scriptPath) },
                        testTag = "exec_device_script_${scriptPath.substringAfterLast("/")}"
                    )
                }
            }
        }

        // 3. Acceso Directo a las 5 Nuevas Herramientas de Mantenimiento (op-cards)
        MaintenanceOperationsSection(
            uiState = uiState,
            onTriggerOperation = onTriggerOperation
        )

        // 4. Gestor de Permisos Hardware (adb / fastboot / edl - Nodo E del Diagrama Mermaid)
        HardwarePermissionManagerCard(
            uiState = uiState,
            onUpdateAdbEndpoint = onUpdateAdbEndpoint,
            onVerifyPermissions = onVerifyPermissions
        )

        // 5. Consola de Salida en Vivo + Terminal Interactiva (/api/execute)
        LiveConsoleOutputCard(
            consoleOutput = uiState.lastConsoleOutput,
            isExecuting = uiState.isExecutingCommand,
            isObsidianMode = isDark,
            terminalInput = uiState.terminalCommandInput,
            onUpdateTerminalInput = onUpdateTerminalInput,
            onExecuteTerminalCmd = onExecuteTerminalCmd
        )
    }
}

@Composable
fun HardwarePermissionManagerCard(
    uiState: KdfUiState,
    onUpdateAdbEndpoint: (String) -> Unit,
    onVerifyPermissions: () -> Unit
) {
    val isDark = uiState.isObsidianMode
    BimetallicCeramicCard(
        isObsidianMode = isDark,
        outerGold = false,
        accentMewtwo = true,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetallicGradientTitle(
                text = "GESTOR DE PERMISOS & PUENTE (ADB / FASTBOOT / EDL 9008)",
                fontSize = 13
            )
            JewelActionButton(
                text = "Verificar / USB OTG",
                icon = Icons.Filled.Security,
                isPrimaryGold = true,
                isMewtwoAccent = true,
                isObsidianMode = isDark,
                onClick = onVerifyPermissions,
                testTag = "btn_verify_hw_permissions"
            )
        }
        BimetallicDivider()
        Text(
            text = "Verifica disponibilidad de adb/fastboot/edl antes de ejecutar, solicita permiso USB OTG al kernel Android y configura depuración inalámbrica TCP/IP.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = uiState.wirelessAdbEndpoint,
            onValueChange = onUpdateAdbEndpoint,
            label = { Text("Endpoint ADB Inalámbrico / Puente TCP (IP:Puerto)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_wireless_adb_endpoint")
        )
    }
}

@Composable
private fun DeviceCategoryGridItem(
    module: DeviceCategoryModule,
    isSelected: Boolean,
    isObsidianMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderBrush = if (isSelected) {
        GoldMetallicBrush
    } else {
        ChromeSilverMetallicBrush
    }
    val bgColor = when {
        isSelected && isObsidianMode -> Color(0xFF2E2448)
        isSelected -> Color(0xFFFFFAEB)
        isObsidianMode -> Color(0xFF1A1726)
        else -> PureCeramicWhite
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(borderBrush)
            .padding(if (isSelected) 2.dp else 1.dp)
            .background(bgColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag("category_card_${module.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Bimetallic3DIcon(
                icon = iconForDeviceCategory(module.id),
                contentDescription = module.title,
                size = 38.dp,
                goldOuterRing = isSelected,
                activeMewtwoGlow = isSelected,
                isObsidianMode = isObsidianMode
            )
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) {
                    if (isObsidianMode) HighGlossGoldHighlight else DeepGoldShadow
                } else {
                    if (isObsidianMode) SpecularWhite else GraphiteCharcoalText
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = module.id,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = if (isSelected) MewtwoPsychicPurple else LeadGreySecondaryText
            )
        }
    }
}

@Composable
fun MaintenanceOperationsSection(
    uiState: KdfUiState,
    onTriggerOperation: (MaintenanceOperation) -> Unit
) {
    val isDark = uiState.isObsidianMode

    BimetallicCeramicCard(
        isObsidianMode = isDark,
        outerGold = true,
        accentMewtwo = false,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetallicGradientTitle(
                text = "🛠 HERRAMIENTAS DE MANTENIMIENTO & UTILIDAD",
                fontSize = 13
            )
            Text(
                text = "5 Módulos Universales",
                style = MaterialTheme.typography.labelSmall,
                color = ImperialGoldPrimary
            )
        }
        Text(
            text = "Módulos propuestos: Safe Mode Manager, App Blocker, Memory Cleaner, Bootloader Unlock y EDL Flasher.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
        )
        BimetallicDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KdfArchitectureCatalog.maintenanceOperations.forEach { op ->
                MaintenanceOperationCard(
                    operation = op,
                    isObsidianMode = isDark,
                    onExecuteClick = { onTriggerOperation(op) }
                )
            }
        }
    }
}

@Composable
private fun MaintenanceOperationCard(
    operation: MaintenanceOperation,
    isObsidianMode: Boolean,
    onExecuteClick: () -> Unit
) {
    val isBootloaderCategory = operation.category.equals("Bootloader", ignoreCase = true)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isBootloaderCategory) GoldMetallicBrush else ChromeSilverMetallicBrush)
            .padding(1.5.dp)
            .background(
                if (isObsidianMode) Color(0xFF1E1A2B) else PearlIvoryCanvas,
                RoundedCornerShape(10.5.dp)
            )
            .padding(10.dp)
            .testTag("op_card_${operation.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Bimetallic3DIcon(
                icon = iconForMaintenanceOp(operation.id),
                contentDescription = operation.visibleName,
                size = 42.dp,
                goldOuterRing = isBootloaderCategory,
                activeMewtwoGlow = operation.id == "app_blocker" || operation.id == "edl_flasher",
                isObsidianMode = isObsidianMode
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${operation.cardTitle} (${operation.englishModuleName})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText
                )
                Text(
                    text = "ID: ${operation.id} • Script: ${operation.scriptPath}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MewtwoPsychicPurple
                )
                Text(
                    text = operation.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isObsidianMode) LiquidSilverLight else LeadGreySecondaryText
                )
                Text(
                    text = "Permisos: ${operation.permissions}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = ImperialGoldPrimary
                )
            }

            JewelActionButton(
                text = "Ejecutar",
                icon = Icons.Filled.PlayArrow,
                isPrimaryGold = true,
                isMewtwoAccent = operation.requiresModalInput,
                isObsidianMode = isObsidianMode,
                onClick = onExecuteClick,
                testTag = "btn_execute_${operation.id}"
            )
        }
    }
}

@Composable
fun LiveConsoleOutputCard(
    consoleOutput: String,
    isExecuting: Boolean,
    isObsidianMode: Boolean,
    terminalInput: String = "",
    onUpdateTerminalInput: (String) -> Unit = {},
    onExecuteTerminalCmd: (String?) -> Unit = {}
) {
    BimetallicCeramicCard(
        isObsidianMode = true,
        accentMewtwo = true,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Terminal,
                    contentDescription = "Terminal KDF-LuX",
                    tint = HighGlossGoldHighlight,
                    modifier = Modifier.size(16.dp)
                )
                MetallicGradientTitle(
                    text = "CONSOLA /API/EXECUTE & TERMINAL DIRECTA",
                    fontSize = 13
                )
            }
            Text(
                text = if (isExecuting) "EJECUTANDO..." else "LISTO",
                style = MaterialTheme.typography.labelSmall,
                color = if (isExecuting) MewtwoPsychicPurple else HighGlossGoldHighlight
            )
        }
        BimetallicDivider()
        Text(
            text = consoleOutput,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                lineHeight = 16.sp
            ),
            color = SpecularWhite,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("console_output_text")
        )

        BimetallicDivider()
        Text(
            text = "Terminal Directa KDF-LuX (Prueba scripts o comandos del sistema):",
            style = MaterialTheme.typography.labelSmall,
            color = HighGlossGoldHighlight
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "./scripts/disable_blocker.sh com.ejemplo.blocker",
                "./scripts/enter_safe_mode.sh",
                "./scripts/clean_memory.sh no",
                "./scripts/unlock_bootloader.sh",
                "./scripts/edl_flasher.sh"
            ).forEach { presetCmd ->
                MetallicChipPill(
                    label = presetCmd,
                    selected = terminalInput == presetCmd,
                    isObsidianMode = true,
                    onClick = { onExecuteTerminalCmd(presetCmd) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = terminalInput,
                onValueChange = onUpdateTerminalInput,
                placeholder = { Text("./scripts/disable_blocker.sh com.ejemplo.blocker") },
                singleLine = true,
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_direct_terminal")
            )
            JewelActionButton(
                text = "Run CLI",
                icon = Icons.Filled.PlayArrow,
                isPrimaryGold = true,
                isMewtwoAccent = true,
                isObsidianMode = true,
                onClick = { onExecuteTerminalCmd(null) },
                testTag = "btn_run_direct_terminal"
            )
        }
    }
}

@Composable
fun MetallicChipPill(
    label: String,
    selected: Boolean,
    isObsidianMode: Boolean,
    onClick: () -> Unit
) {
    val bgBrush = when {
        selected -> GoldMetallicBrush
        else -> ChromeSilverMetallicBrush
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .padding(1.dp)
            .background(
                when {
                    selected -> MewtwoDeepViolet
                    isObsidianMode -> MewtwoObsidianBlack
                    else -> PureCeramicWhite
                },
                RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = when {
                selected -> HighGlossGoldHighlight
                isObsidianMode -> SpecularWhite
                else -> GraphiteCharcoalText
            }
        )
    }
}

/**
 * Ventana Modal Reutilizada (Especificación Sección 4 de KDF-LuX):
 * - Para App Blocker (`app_blocker`): solicita el nombre del paquete mediante un input + selector de paquetes reales instalados, modo (disable-user / uninstall) y confirmación opcional para `fastboot -w`.
 * - Para Mem Cleaner (`mem_cleaner`): pregunta si se quiere hacer wipe userdata (`fastboot -w` sí/no) antes de ejecutar.
 * - Para EDL Flasher (`edl_flasher`): solicita `bootloader.img`, `radio.img` y `prog_firehose_ddr.elf`.
 */
@Composable
fun MaintenanceOperationModal(
    modalState: ActiveModalState,
    installedPackages: List<InstalledAppInfo>,
    isObsidianMode: Boolean,
    onDismiss: () -> Unit,
    onUpdatePackage: (String) -> Unit,
    onUpdateBlockerMode: (String) -> Unit,
    onUpdateWipeConfirm: (Boolean) -> Unit,
    onUpdateEdlParams: (String, String, String) -> Unit,
    onConfirmExecute: () -> Unit
) {
    val op = modalState.operation

    Dialog(onDismissRequest = onDismiss) {
        BimetallicCeramicCard(
            isObsidianMode = isObsidianMode,
            accentMewtwo = true,
            outerGold = true,
            contentPadding = PaddingValues(16.dp)
        ) {
            MetallicGradientTitle(
                text = op.cardTitle.uppercase(),
                fontSize = 15
            )
            Text(
                text = "Script: ${op.scriptPath} • Permisos: ${op.permissions}",
                style = MaterialTheme.typography.labelSmall,
                color = MewtwoPsychicPurple
            )
            BimetallicDivider()

            when (op.id) {
                "app_blocker" -> {
                    Text(
                        text = "Introduce el paquete de la aplicación bloqueadora o selecciona uno detectado en el dispositivo:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = modalState.targetPackage,
                        onValueChange = onUpdatePackage,
                        label = { Text("Nombre del paquete (ej. com.ejemplo.blocker)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_blocker_package")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Modo de neutralización ADB:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImperialGoldPrimary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetallicChipPill(
                            label = "pm disable-user (Desactivar)",
                            selected = modalState.blockerActionMode == "disable",
                            isObsidianMode = isObsidianMode,
                            onClick = { onUpdateBlockerMode("disable") }
                        )
                        MetallicChipPill(
                            label = "pm uninstall (Eliminar)",
                            selected = modalState.blockerActionMode == "uninstall",
                            isObsidianMode = isObsidianMode,
                            onClick = { onUpdateBlockerMode("uninstall") }
                        )
                    }

                    if (installedPackages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Paquetes reales detectados en este dispositivo (toca para elegir):",
                            style = MaterialTheme.typography.labelSmall,
                            color = LeadGreySecondaryText
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 120.dp)
                                .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            items(installedPackages) { pkg ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onUpdatePackage(pkg.packageName) }
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = pkg.appLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText,
                                        maxLines = 1,
                                        modifier = Modifier.weight(0.45f)
                                    )
                                    Text(
                                        text = pkg.packageName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MewtwoPsychicPurple,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(0.55f)
                                    )
                                }
                            }
                        }
                    }
                }

                "mem_cleaner" -> {
                    Text(
                        text = "Esta operación limpiará /data/cache, /cache y liberará procesos secundarios en RAM de inmediato.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText
                    )
                }

                "edl_flasher" -> {
                    Text(
                        text = "Configura las imágenes de bajo nivel para flashear vía Qualcomm EDL 9008 (edl.py / QFIL):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = modalState.edlBootloaderImg,
                        onValueChange = {
                            onUpdateEdlParams(it, modalState.edlRadioImg, modalState.edlFirehoseLoader)
                        },
                        label = { Text("Imagen Bootloader (bootloader.img)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = modalState.edlRadioImg,
                        onValueChange = {
                            onUpdateEdlParams(modalState.edlBootloaderImg, it, modalState.edlFirehoseLoader)
                        },
                        label = { Text("Imagen Radio / Modem (radio.img)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = modalState.edlFirehoseLoader,
                        onValueChange = {
                            onUpdateEdlParams(modalState.edlBootloaderImg, modalState.edlRadioImg, it)
                        },
                        label = { Text("Firehose Programmer (.elf / .mbn)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (op.supportsWipeUserdata) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (modalState.confirmWipeUserdata) {
                                TelemetryCriticalCrimson.copy(alpha = 0.14f)
                            } else {
                                ImperialGoldPrimary.copy(alpha = 0.10f)
                            }
                        )
                        .border(1.dp, GoldMetallicBrush, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Ejecutar también fastboot -w (Wipe Userdata)?",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (modalState.confirmWipeUserdata) TelemetryCriticalCrimson else ImperialGoldPrimary
                        )
                        Text(
                            text = if (modalState.confirmWipeUserdata) {
                                "SÍ CONFIRMADO: Se ejecutará fastboot -w tras la operación."
                            } else {
                                "NO (Seguro): Se conservarán los datos de usuario."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isObsidianMode) LiquidSilverLight else LeadGreySecondaryText
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    BimetallicPhysicalSwitch(
                        checked = modalState.confirmWipeUserdata,
                        onCheckedChange = onUpdateWipeConfirm,
                        testTag = "modal_wipe_switch"
                    )
                }
            }

            BimetallicDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JewelActionButton(
                    text = "Cancelar",
                    isPrimaryGold = false,
                    isObsidianMode = isObsidianMode,
                    onClick = onDismiss,
                    testTag = "modal_cancel_button"
                )
                Spacer(modifier = Modifier.width(8.dp))
                JewelActionButton(
                    text = "Confirmar y Ejecutar",
                    icon = Icons.Filled.PlayArrow,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isObsidianMode,
                    onClick = onConfirmExecute,
                    testTag = "modal_confirm_execute_button"
                )
            }
        }
    }
}
