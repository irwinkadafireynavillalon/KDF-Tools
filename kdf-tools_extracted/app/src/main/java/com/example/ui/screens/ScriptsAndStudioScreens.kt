package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuditLogEntity
import com.example.data.BioScienceRecordEntity
import com.example.data.GeneratedImageEntity
import com.example.ui.KdfUiState
import com.example.ui.components.Bimetallic3DIcon
import com.example.ui.components.BimetallicCeramicCard
import com.example.ui.components.BimetallicDivider
import com.example.ui.components.JewelActionButton
import com.example.ui.components.MetallicGradientTitle
import com.example.ui.theme.ChromeSilverMetallicBrush
import com.example.ui.theme.GoldMetallicBrush
import com.example.ui.theme.GraphiteCharcoalText
import com.example.ui.theme.HighGlossGoldHighlight
import com.example.ui.theme.ImperialGoldPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LeadGreySecondaryText
import com.example.ui.theme.LiquidSilverLight
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.SpecularWhite
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Scripts (.sh) y Configuración YAML (`config/categories.yaml`, `config/device_modules.yaml`)
 * Permite ver, editar, guardar y ejecutar directamente los scripts reales en `KDF-LuX/scripts/`.
 */
@Composable
fun ScriptsAndYamlEditorScreen(
    uiState: KdfUiState,
    onSelectFile: (File) -> Unit,
    onUpdateContent: (String) -> Unit,
    onSaveFile: () -> Unit,
    onRunScript: () -> Unit
) {
    val isDark = uiState.isObsidianMode

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = true,
            accentMewtwo = true,
            contentPadding = PaddingValues(12.dp)
        ) {
            MetallicGradientTitle(
                text = "GESTOR DE SCRIPTS (.SH) & CONFIGURACIÓN (CATEGORIES.YAML)",
                fontSize = 14
            )
            Text(
                text = "Todos los scripts en KDF-LuX/scripts/ están creados con permisos ejecutables (chmod +x) y sincronizados con config/categories.yaml.",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
            )
            BimetallicDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.scriptFiles.forEach { file ->
                    val isSelected = uiState.selectedFile?.absolutePath == file.absolutePath
                    MetallicChipPill(
                        label = file.name,
                        selected = isSelected,
                        isObsidianMode = isDark,
                        onClick = { onSelectFile(file) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            uiState.selectedFile?.let { file ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Archivo activo: ${file.name}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) HighGlossGoldHighlight else GraphiteCharcoalText
                        )
                        Text(
                            text = "Ruta: KDF-LuX/${file.parentFile?.name}/${file.name} • Ejecutable: ${file.canExecute()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoPsychicPurple
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        JewelActionButton(
                            text = "Guardar",
                            icon = Icons.Filled.Save,
                            isPrimaryGold = false,
                            isObsidianMode = isDark,
                            onClick = onSaveFile,
                            testTag = "btn_save_script"
                        )
                        if (file.extension == "sh") {
                            JewelActionButton(
                                text = "Ejecutar .sh",
                                icon = Icons.Filled.PlayArrow,
                                isPrimaryGold = true,
                                isMewtwoAccent = true,
                                isObsidianMode = isDark,
                                onClick = onRunScript,
                                testTag = "btn_run_selected_script"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.selectedFileContent,
                    onValueChange = onUpdateContent,
                    textStyle = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp, max = 360.dp)
                        .testTag("script_editor_textfield")
                )
            }
        }

        // Diagrama de Arquitectura KDF-LuX interactivo
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = false,
            contentPadding = PaddingValues(12.dp)
        ) {
            MetallicGradientTitle(
                text = "ARQUITECTURA FINAL KDF-LUX (FLUJO UI → MOTOR → SCRIPTS → DISPOSITIVO)",
                fontSize = 13
            )
            BimetallicDivider()
            Text(
                text = """
                    ┌──────────────────────────────────────────────────────────────┐
                    │ UI Bimetálica: 6 Categorías Dispositivo + 5 Tarjetas Op-Card │
                    └───────────────┬──────────────────────────────┬───────────────┘
                                    │                              │
                                    ▼                              ▼
                    ┌───────────────────────────────┐  ┌───────────────────────────┐
                    │ config/categories.yaml        │  │ Gestor Permisos Hardware  │
                    │ config/device_modules.yaml    │  │ (ADB / Fastboot / EDL)    │
                    └───────────────┬───────────────┘  └─────────────┬─────────────┘
                                    │                                │
                                    ▼                                ▼
                    ┌──────────────────────────────────────────────────────────────┐
                    │ scripts/enter_safe_mode.sh   │ scripts/disable_blocker.sh    │
                    │ scripts/clean_memory.sh      │ scripts/unlock_bootloader.sh  │
                    │ scripts/edl_flasher.sh       │ scripts/flash_aosp_gsi.sh     │
                    └───────────────────────────────┬──────────────────────────────┘
                                                    ▼
                    ┌──────────────────────────────────────────────────────────────┐
                    │ Dispositivo Android & Auditoría en logs/audit_execution.log  │
                    └──────────────────────────────────────────────────────────────┘
                """.trimIndent(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.5.sp,
                    lineHeight = 13.sp
                ),
                color = if (isDark) HighGlossGoldHighlight else GraphiteCharcoalText
            )
        }

        LiveConsoleOutputCard(
            consoleOutput = uiState.lastConsoleOutput,
            isExecuting = uiState.isExecutingCommand,
            isObsidianMode = isDark
        )
    }
}

/**
 * Estudio IA 4K (`gemini-3-pro-image-preview` con selector explícito `1K`, `2K`, `4K`) +
 * Consola / Chat Técnico Gemini (`gemini-3.5-flash`).
 */
@Composable
fun AiStudioAndConsoleScreen(
    uiState: KdfUiState,
    generatedImages: List<GeneratedImageEntity>,
    onUpdatePrompt: (String) -> Unit,
    onSelectImageSize: (String) -> Unit,
    onSelectAspectRatio: (String) -> Unit,
    onGenerateImage: () -> Unit,
    onSendChatMessage: (String) -> Unit
) {
    val isDark = uiState.isObsidianMode
    var chatInput by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. Generador de Imágenes de Alta Calidad (gemini-3-pro-image-preview con 1K, 2K, 4K)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            accentMewtwo = true,
            outerGold = true,
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Bimetallic3DIcon(
                    icon = Icons.Filled.Image,
                    contentDescription = "Generador IA",
                    size = 38.dp,
                    goldOuterRing = true,
                    activeMewtwoGlow = true,
                    isObsidianMode = isDark
                )
                Column(modifier = Modifier.weight(1f)) {
                    MetallicGradientTitle(
                        text = "ESTUDIO DE IMÁGENES KDF • GEMINI-3-PRO-IMAGE-PREVIEW",
                        fontSize = 13
                    )
                    Text(
                        text = "Genera emblemas bimetálicos, bootanimations, esquemas de hardware o fondos Mewtwo en 1K, 2K y 4K.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                    )
                }
            }
            BimetallicDivider()

            // Selector obligatorio de tamaño de imagen: 1K, 2K y 4K
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Resolución de Imagen (imageSize):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ImperialGoldPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("1K", "2K", "4K").forEach { sizeOption ->
                        MetallicChipPill(
                            label = sizeOption,
                            selected = uiState.selectedImageSize == sizeOption,
                            isObsidianMode = isDark,
                            onClick = { onSelectImageSize(sizeOption) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Proporción (aspectRatio):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) SpecularWhite else GraphiteCharcoalText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("1:1", "16:9", "9:16", "4:3").forEach { ratio ->
                        MetallicChipPill(
                            label = ratio,
                            selected = uiState.selectedAspectRatio == ratio,
                            isObsidianMode = isDark,
                            onClick = { onSelectAspectRatio(ratio) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.imagePrompt,
                onValueChange = onUpdatePrompt,
                label = { Text("Prompt de diseño bimetálico / hardware / Mewtwo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_gemini_image_prompt"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.isGeneratingImage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MewtwoPsychicPurple,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Renderizando ${uiState.selectedImageSize}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoPsychicPurple
                        )
                    }
                } else {
                    Text(
                        text = uiState.imageGenerationStatus.ifBlank {
                            "Modelo activo: gemini-3-pro-image-preview (${uiState.selectedImageSize})"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) HighGlossGoldHighlight else LeadGreySecondaryText,
                        modifier = Modifier.weight(1f)
                    )
                }

                JewelActionButton(
                    text = "Generar ${uiState.selectedImageSize}",
                    icon = Icons.Filled.AutoAwesome,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    enabled = !uiState.isGeneratingImage,
                    onClick = onGenerateImage,
                    testTag = "btn_generate_image_pro"
                )
            }

            // Galería de imágenes generadas
            if (generatedImages.isNotEmpty()) {
                BimetallicDivider()
                Text(
                    text = "Renders Generados en Almacenamiento Local:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ImperialGoldPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    generatedImages.forEach { item ->
                        val bmp = remember(item.filePath) {
                            val f = File(item.filePath)
                            if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
                        }
                        if (bmp != null) {
                            Column(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.5.dp, GoldMetallicBrush, RoundedCornerShape(10.dp))
                                    .background(MewtwoObsidianBlack)
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = item.prompt,
                                    modifier = Modifier
                                        .size(124.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.imageSize} • ${item.aspectRatio}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HighGlossGoldHighlight
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Consola de Chat Gemini (Arquitectura Sección 6: Consola de salida / Chat Gemini)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = false,
            contentPadding = PaddingValues(12.dp)
        ) {
            MetallicGradientTitle(
                text = "CONSOLA TÉCNICA & CHAT GEMINI (INGENIERÍA KDF-LUX)",
                fontSize = 13
            )
            BimetallicDivider()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.chatMessages.takeLast(6).forEach { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (msg.isUser) {
                                    MewtwoPsychicPurple.copy(alpha = 0.16f)
                                } else {
                                    if (isDark) Color(0xFF161322) else PearlIvoryCanvas
                                }
                            )
                            .border(
                                1.dp,
                                if (msg.isUser) GoldMetallicBrush else ChromeSilverMetallicBrush,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = if (msg.isUser) "OPERADOR KDF:" else "NÚCLEO GEMINI KDF-LUX:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (msg.isUser) MewtwoPsychicPurple else ImperialGoldPrimary
                            )
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) SpecularWhite else GraphiteCharcoalText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Consulta comando ADB, EDL test-point o script...") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_gemini_chat")
                )
                JewelActionButton(
                    text = if (uiState.isChatLoading) "..." else "Enviar",
                    icon = Icons.Filled.Send,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    enabled = !uiState.isChatLoading,
                    onClick = {
                        onSendChatMessage(chatInput)
                        chatInput = ""
                    },
                    testTag = "btn_send_gemini_chat"
                )
            }
        }

        // Aviso de seguridad de clave API para prototipos
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = false,
            contentPadding = PaddingValues(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.security_warning_prototype),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = LeadGreySecondaryText
            )
        }
    }
}

/**
 * Laboratorio de Bio-Ciencia (Bioscience Skills) + Telemetría Física de Sensores + Historial de Auditoría (`logs/`).
 */
@Composable
fun BioScienceAndLogsScreen(
    uiState: KdfUiState,
    bioRecords: List<BioScienceRecordEntity>,
    auditLogs: List<AuditLogEntity>,
    onUpdateBioName: (String) -> Unit,
    onUpdateBioSeq: (String) -> Unit,
    onRunBioAnalysis: () -> Unit,
    onClearLogs: () -> Unit
) {
    val isDark = uiState.isObsidianMode
    val hw = uiState.liveTelemetry

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. Laboratorio Bio-Informático y Telemetría de Bio-Sensores (Bioscience Skill)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            accentMewtwo = true,
            outerGold = true,
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Bimetallic3DIcon(
                    icon = Icons.Filled.Biotech,
                    contentDescription = "Bio-Lab",
                    size = 40.dp,
                    goldOuterRing = true,
                    activeMewtwoGlow = true,
                    isObsidianMode = isDark
                )
                Column(modifier = Modifier.weight(1f)) {
                    MetallicGradientTitle(
                        text = "LABORATORIO DE BIO-CIENCIA & TELEMETRÍA FÍSICA",
                        fontSize = 13
                    )
                    Text(
                        text = "Análisis molecular ADN/ARN (GC%, Temperatura de Fusión Tm, Masa en Daltons, Traducción Peptídica) + Sensores en Vivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                    )
                }
            }
            BimetallicDivider()

            // Lectura real de sensores físicos del dispositivo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SensorMetricBox(
                    title = "CAMPO MAGNÉTICO",
                    value = String.format(Locale.US, "%.1f µT", hw.geomagneticFieldUt),
                    sub = "${hw.activeSensorsCount} sensores activos",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                SensorMetricBox(
                    title = "ACELERACIÓN",
                    value = String.format(Locale.US, "%.2f m/s²", hw.accelerometerMag),
                    sub = "Vector 3D real",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                SensorMetricBox(
                    title = "VOLTAJE / TEMP",
                    value = "${hw.batteryVoltageMv} mV",
                    sub = " Térmica: ${hw.batteryTempC}°C",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.bioSampleName,
                onValueChange = onUpdateBioName,
                label = { Text("Identificador de Muestra Bio-Científica") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.bioSequenceInput,
                onValueChange = onUpdateBioSeq,
                label = { Text("Secuencia Nucleotídica (ADN / ARN - A, C, G, T, U)") },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_bio_sequence")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                JewelActionButton(
                    text = if (uiState.isAnalyzingBio) "Analizando..." else "Analizar Secuencia & Bio-Telemetría",
                    icon = Icons.Filled.Science,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    enabled = !uiState.isAnalyzingBio,
                    onClick = onRunBioAnalysis,
                    testTag = "btn_run_bioscience"
                )
            }

            if (bioRecords.isNotEmpty()) {
                BimetallicDivider()
                bioRecords.take(3).forEach { rec ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, GoldMetallicBrush, RoundedCornerShape(10.dp))
                            .background(if (isDark) Color(0xFF171324) else PearlIvoryCanvas)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "${rec.sampleName} • GC: ${String.format(Locale.US, "%.1f", rec.gcContentPercent)}% • Tm: ${String.format(Locale.US, "%.1f", rec.meltingTempC)}°C • Masa: ${String.format(Locale.US, "%.0f", rec.molecularWeightDa)} Da",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImperialGoldPrimary
                            )
                            Text(
                                text = "Cadena Peptídica: ${rec.proteinTranslation}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MewtwoPsychicPurple
                            )
                            Text(
                                text = rec.aiInterpretation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) SpecularWhite else GraphiteCharcoalText
                            )
                        }
                    }
                }
            }
        }

        // 2. Registro de Auditoría (logs/audit_execution.log + Base de Datos Room)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = false,
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
                    Bimetallic3DIcon(
                        icon = Icons.Filled.History,
                        contentDescription = "Logs",
                        size = 34.dp,
                        goldOuterRing = false,
                        isObsidianMode = isDark
                    )
                    MetallicGradientTitle(
                        text = "HISTORIAL DE AUDITORÍA (LOGS/)",
                        fontSize = 13
                    )
                }
                JewelActionButton(
                    text = "Limpiar Logs",
                    icon = Icons.Filled.DeleteSweep,
                    isPrimaryGold = false,
                    isObsidianMode = isDark,
                    onClick = onClearLogs,
                    testTag = "btn_clear_audit_logs"
                )
            }
            BimetallicDivider()

            if (auditLogs.isEmpty()) {
                Text(
                    text = "No hay ejecuciones registradas aún. Ejecuta cualquier herramienta de mantenimiento o script de dispositivo para auditar su salida.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LeadGreySecondaryText
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    auditLogs.take(15).forEach { log ->
                        val dateStr = remember(log.timestamp) {
                            SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestamp))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0xFF15121F) else PearlIvoryCanvas)
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "[$dateStr] ${log.operationName}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDark) HighGlossGoldHighlight else GraphiteCharcoalText
                                    )
                                    Text(
                                        text = log.telemetryStatusAfter,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MewtwoPsychicPurple
                                    )
                                }
                                Text(
                                    text = "$ ${log.commandExecuted}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ImperialGoldPrimary
                                )
                                Text(
                                    text = log.outputLog.trim(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                    color = if (isDark) LiquidSilverLight else LeadGreySecondaryText,
                                    maxLines = 5
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorMetricBox(
    title: String,
    value: String,
    sub: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GoldMetallicBrush, RoundedCornerShape(8.dp))
            .background(if (isDark) Color(0xFF161322) else PearlIvoryCanvas)
            .padding(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = ImperialGoldPrimary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) SpecularWhite else GraphiteCharcoalText
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = MewtwoPsychicPurple
            )
        }
    }
}
