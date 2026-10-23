package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.UpscalingProfile
import com.example.ui.ChatMessage
import com.example.ui.KdfUiState
import com.example.ui.components.Bimetallic3DIcon
import com.example.ui.components.BimetallicCeramicCard
import com.example.ui.components.BimetallicDivider
import com.example.ui.components.BimetallicPhysicalSwitch
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
import com.example.ui.theme.MewtwoAuraGlow
import com.example.ui.theme.MewtwoDeepViolet
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.MewtwoRoyalIndigo
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.PolishedChrome
import com.example.ui.theme.PureCeramicWhite
import com.example.ui.theme.SpecularWhite
import com.example.ui.theme.TelemetrySafeEmerald
import java.io.File
import java.util.Locale

/**
 * Botón Flotante Compacto Bimetálico para abrir el Chatbot KDF-LuX desde cualquier pantalla
 * sin quitar visibilidad a la aplicación.
 */
@Composable
fun CompactFloatingChatbotButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(10.dp, CircleShape)
            .clip(RoundedCornerShape(22.dp))
            .background(GoldMetallicBrush)
            .padding(1.5.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(MewtwoObsidianBlack, MewtwoDeepViolet, MewtwoRoyalIndigo)
                ),
                RoundedCornerShape(21.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("fab_open_chatbot"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SmartToy,
                contentDescription = "Abrir Chatbot KDF-LuX",
                tint = HighGlossGoldHighlight,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Chatbot IA",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = SpecularWhite
            )
        }
    }
}

/**
 * Ventana Modal del Chatbot KDF-LuX (Mewtwo Bimetallic Chatbot: IA Neuronal Gemini + Efecto Eliza + Comandos Directos)
 */
@Composable
fun KdfBimetallicChatbotModal(
    uiState: KdfUiState,
    onSelectMode: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.84f)
                .clip(RoundedCornerShape(20.dp))
                .background(GoldMetallicBrush)
                .padding(2.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(MewtwoObsidianBlack, Color(0xFF151026), MewtwoObsidianBlack)
                    ),
                    RoundedCornerShape(18.dp)
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Cabecera del Chatbot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Bimetallic3DIcon(
                            icon = Icons.Filled.SmartToy,
                            contentDescription = "Chatbot KDF-LuX",
                            size = 38.dp,
                            goldOuterRing = true,
                            activeMewtwoGlow = true,
                            isObsidianMode = true
                        )
                        Column {
                            MetallicGradientTitle(
                                text = "CHATBOT KDF-LUX • IA & HEURÍSTICA",
                                fontSize = 14
                            )
                            Text(
                                text = "KDF-Tools: Spark Core + Gemini 3.8 Flash + Ejecución de Scripts",
                                style = MaterialTheme.typography.labelSmall,
                                color = MewtwoAuraGlow
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_chatbot_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar Chatbot",
                            tint = HighGlossGoldHighlight
                        )
                    }
                }

                BimetallicDivider()

                // Cuerpo reutilizable del Chatbot
                Box(modifier = Modifier.weight(1f)) {
                    KdfChatbotConversationBody(
                        uiState = uiState,
                        onSelectMode = onSelectMode,
                        onSendMessage = onSendMessage,
                        maxListHeight = 420
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta Interactiva del Chatbot KDF-LuX para usar directamente en la pantalla principal o en Estudio IA
 */
@Composable
fun KdfInteractiveChatbotCard(
    uiState: KdfUiState,
    onSelectMode: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenFullModal: () -> Unit
) {
    val isDark = uiState.isObsidianMode
    BimetallicCeramicCard(
        isObsidianMode = isDark,
        outerGold = true,
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Bimetallic3DIcon(
                    icon = Icons.Filled.Forum,
                    contentDescription = "Chatbot KDF-Tools",
                    size = 40.dp,
                    goldOuterRing = true,
                    activeMewtwoGlow = true,
                    isObsidianMode = isDark
                )
                Column {
                    MetallicGradientTitle(
                        text = "CHATBOT AUTÓNOMO KDF-LUX (GEMINI + ELIZA)",
                        fontSize = 14
                    )
                    Text(
                        text = "Ejecuta diagnósticos, scripts, superresolución y consultas en vivo.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) MewtwoAuraGlow else LeadGreySecondaryText
                    )
                }
            }
            JewelActionButton(
                text = "Expandir Chat",
                icon = Icons.Filled.SmartToy,
                isPrimaryGold = false,
                isMewtwoAccent = true,
                isObsidianMode = isDark,
                onClick = onOpenFullModal,
                testTag = "btn_expand_chatbot_card"
            )
        }

        BimetallicDivider()

        KdfChatbotConversationBody(
            uiState = uiState,
            onSelectMode = onSelectMode,
            onSendMessage = onSendMessage,
            maxListHeight = 250
        )
    }
}

@Composable
private fun KdfChatbotConversationBody(
    uiState: KdfUiState,
    onSelectMode: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    maxListHeight: Int
) {
    val isDark = uiState.isObsidianMode
    var chatInput by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Selector de Modo del Motor de Chat (Híbrido vs Efecto Eliza vs Gemini Puro)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "HYBRID_AI_ELIZA" to "⚡☁️ Spark + Gemini (Híbrido)",
                "ELIZA_RULES_ONLY" to "⚡ Spark Core (Heurística <1ms)",
                "GEMINI_NEURAL_ONLY" to "☁️ Gemini 3.8 Flash (Cloud)"
            ).forEach { (modeId, label) ->
                MetallicChipPill(
                    label = label,
                    selected = uiState.chatbotMode == modeId,
                    isObsidianMode = isDark,
                    onClick = { onSelectMode(modeId) }
                )
            }
        }

        // Botones rápidos de comando directo en el Chatbot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Revisar cortos USB",
                "Activar Superresolución FSR",
                "Auto-reparar scripts",
                "Limpiar memoria y caché",
                "¿Qué es Efecto Eliza y AI Washing?"
            ).forEach { quickPrompt ->
                MetallicChipPill(
                    label = quickPrompt,
                    selected = false,
                    isObsidianMode = isDark,
                    onClick = { onSendMessage(quickPrompt) }
                )
            }
        }

        // Historial de Mensajes del Chatbot
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp, max = maxListHeight.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) Color(0xFF09070F) else PearlIvoryCanvas)
                .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(10.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.chatMessages, key = { it.id }) { msg ->
                ChatbotBubbleItem(msg = msg, isDark = isDark)
            }
            if (uiState.isChatLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = HighGlossGoldHighlight,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Procesando respuesta neuronal y telemetría...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoAuraGlow
                        )
                    }
                }
            }
        }

        // Campo de texto y botón Enviar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                label = { Text("Escribe al Chatbot KDF-LuX (ej. revisar cortos, activar FSR, ADB)...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_kdf_chatbot"),
                singleLine = true
            )
            JewelActionButton(
                text = "Enviar",
                icon = Icons.AutoMirrored.Filled.Send,
                isPrimaryGold = true,
                isMewtwoAccent = true,
                isObsidianMode = isDark,
                onClick = {
                    if (chatInput.isNotBlank()) {
                        onSendMessage(chatInput)
                        chatInput = ""
                    }
                },
                testTag = "btn_send_kdf_chatbot"
            )
        }
    }
}

@Composable
private fun ChatbotBubbleItem(
    msg: ChatMessage,
    isDark: Boolean
) {
    val bg = when {
        msg.isUser -> MewtwoDeepViolet.copy(alpha = 0.78f)
        isDark -> Color(0xFF161224)
        else -> PureCeramicWhite
    }
    val border = if (msg.isUser) GoldMetallicBrush else ChromeSilverMetallicBrush

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (msg.isUser) "TÚ (OPERADOR KDF)" else "♛ CHATBOT KDF-LUX",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = if (msg.isUser) HighGlossGoldHighlight else MewtwoAuraGlow
                )
                Text(
                    text = msg.engineBadge,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = ImperialGoldPrimary
                )
            }
            if (!msg.matchedRuleOrAction.isNullOrBlank()) {
                Text(
                    text = "⚙ ${msg.matchedRuleOrAction}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.5.sp
                    ),
                    color = TelemetrySafeEmerald
                )
            }
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.5.sp),
                color = if (isDark || msg.isUser) SpecularWhite else GraphiteCharcoalText
            )
        }
    }
}

/**
 * BLOQUE 1: SUPERRESOLUCIÓN / AI UPSCALING (GSR, FSR, DLSS, MetalFX) +
 * FOTOGRAFÍA COMPUTACIONAL (ISP HDR, Denoise, Retrato) + ACELERACIÓN POR HARDWARE (NNAPI / AICore / GPU)
 */
@Composable
fun HardwareVisualSuperResolutionCard(
    uiState: KdfUiState,
    onSelectUpscalingProfile: (UpscalingProfile) -> Unit,
    onUpdateSharpness: (Float) -> Unit,
    onToggleHdr: (Boolean) -> Unit,
    onToggleDenoise: (Boolean) -> Unit,
    onToggleMicroContrast: (Boolean) -> Unit,
    onSelectHardwareDelegate: (String) -> Unit,
    onRunSuperResolution: (Uri?) -> Unit,
    onApplyProcessedAsWallpaper: (String) -> Unit
) {
    val isDark = uiState.isObsidianMode
    val vs = uiState.visualSettings
    val hw = uiState.hardwareAccelerator

    val galleryUpscaleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onRunSuperResolution(uri)
        }
    }

    val processedBitmap = remember(vs.lastProcessedImagePath) {
        if (!vs.lastProcessedImagePath.isNullOrBlank()) {
            val f = File(vs.lastProcessedImagePath)
            if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
        } else {
            null
        }
    }

    BimetallicCeramicCard(
        isObsidianMode = isDark,
        outerGold = true,
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Bimetallic3DIcon(
                    icon = Icons.Filled.HighQuality,
                    contentDescription = "Superresolución GPU/NPU",
                    size = 42.dp,
                    goldOuterRing = true,
                    activeMewtwoGlow = true,
                    isObsidianMode = isDark
                )
                Column {
                    MetallicGradientTitle(
                        text = "SUPERRESOLUCIÓN GPU/NPU & FOTOGRAFÍA COMPUTACIONAL ISP",
                        fontSize = 13
                    )
                    Text(
                        text = "${hw.gpuRendererInfo} • ${hw.estimatedTopsCapability}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MewtwoAuraGlow
                    )
                }
            }
            JewelActionButton(
                text = if (uiState.isUpscalingImage) "Escalando..." else "Aplicar Upscaling",
                icon = Icons.Filled.AutoFixHigh,
                isPrimaryGold = true,
                isMewtwoAccent = true,
                isObsidianMode = isDark,
                onClick = { onRunSuperResolution(null) },
                testTag = "btn_run_gpu_super_resolution"
            )
        }

        BimetallicDivider()

        // 1. Selector de Algoritmos de Superresolución / AI Upscaling
        Text(
            text = "1. Superresolución / AI Upscaling (Escalado Espacial y Reconstrucción por GPU/NPU):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = HighGlossGoldHighlight
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            UpscalingProfile.entries.forEach { profile ->
                MetallicChipPill(
                    label = "${profile.title} [${profile.badge}]",
                    selected = vs.selectedProfile == profile,
                    isObsidianMode = isDark,
                    onClick = { onSelectUpscalingProfile(profile) }
                )
            }
        }
        Text(
            text = vs.selectedProfile.description,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nitidez RCAS / Realce de Bordes en Subpíxeles: ${(vs.sharpnessIntensity * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SpecularWhite
            )
            JewelActionButton(
                text = "Escalar Foto de Galería",
                icon = Icons.Filled.PhotoLibrary,
                isPrimaryGold = false,
                isMewtwoAccent = true,
                isObsidianMode = isDark,
                onClick = {
                    galleryUpscaleLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                testTag = "btn_upscale_gallery_photo"
            )
        }
        Slider(
            value = vs.sharpnessIntensity,
            onValueChange = onUpdateSharpness,
            valueRange = 0.15f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = HighGlossGoldHighlight,
                activeTrackColor = MewtwoPsychicPurple,
                inactiveTrackColor = PolishedChrome
            ),
            modifier = Modifier.testTag("slider_rcas_sharpness")
        )

        BimetallicDivider()

        // 2. Fotografía Computacional (ISP / NPU Pipeline)
        Text(
            text = "2. Fotografía Computacional Nativa (Procesamiento Automático ISP / NPU / DSP):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = HighGlossGoldHighlight
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IspToggleBox(
                title = "HDR Tone Mapping",
                subtitle = "Rango dinámico ISP",
                checked = vs.computationalHdrEnabled,
                onCheckedChange = onToggleHdr,
                isDark = isDark,
                modifier = Modifier.weight(1f),
                testTag = "switch_isp_hdr"
            )
            IspToggleBox(
                title = "ISP Spatial Denoise",
                subtitle = "Reducción de ruido",
                checked = vs.ispDenoiseEnabled,
                onCheckedChange = onToggleDenoise,
                isDark = isDark,
                modifier = Modifier.weight(1f),
                testTag = "switch_isp_denoise"
            )
            IspToggleBox(
                title = "Modo Retrato / Claridad",
                subtitle = "Micro-contraste NPU",
                checked = vs.portraitMicroContrastEnabled,
                onCheckedChange = onToggleMicroContrast,
                isDark = isDark,
                modifier = Modifier.weight(1f),
                testTag = "switch_isp_portrait"
            )
        }

        BimetallicDivider()

        // 3. Aceleración por Hardware / APIs Nativas de ML (NNAPI / AICore / Vulkan / MetalFX-Equiv)
        Text(
            text = "3. Aceleración por Hardware / APIs Nativas de ML (Delegación al Procesador Neuronal):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
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
                "NNAPI + GPU Skia/Vulkan",
                "Android AICore (NPU Delegate)",
                "DSP Hexagon / SIMD Vector",
                "Equivalente Core ML / DirectML Bridge"
            ).forEach { delegate ->
                MetallicChipPill(
                    label = delegate,
                    selected = vs.hardwareAccelerationDelegate == delegate,
                    isObsidianMode = isDark,
                    onClick = { onSelectHardwareDelegate(delegate) }
                )
            }
        }

        // Estado del Pipeline Gráfico y Vista Previa del Resultado Escalado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
                .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "• Estado NPU/ML: ${hw.npuNnapiStatus}  |  • Pipeline ISP: ${hw.dspIspPipeline}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SpecularWhite
                )
                Text(
                    text = "• Métrica de Procesamiento: ${vs.lastProcessingMetrics}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = TelemetrySafeEmerald
                )
                if (processedBitmap != null && !vs.lastProcessedImagePath.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            bitmap = processedBitmap.asImageBitmap(),
                            contentDescription = "Imagen Escalada con Superresolución",
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, GoldMetallicBrush, RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Render Superresolución + ISP Generado:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = HighGlossGoldHighlight
                            )
                            JewelActionButton(
                                text = "Usar Imagen Mejorada como Fondo",
                                icon = Icons.Filled.CameraEnhance,
                                isPrimaryGold = true,
                                isMewtwoAccent = true,
                                isObsidianMode = isDark,
                                onClick = { onApplyProcessedAsWallpaper(vs.lastProcessedImagePath) },
                                testTag = "btn_use_upscaled_as_wallpaper"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IspToggleBox(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
            .border(
                1.dp,
                if (checked) GoldMetallicBrush else ChromeSilverMetallicBrush,
                RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = if (checked) HighGlossGoldHighlight else LiquidSilverLight,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = LeadGreySecondaryText
            )
            BimetallicPhysicalSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                testTag = testTag
            )
        }
    }
}

/**
 * BLOQUE 2: HEURÍSTICAS / SISTEMAS BASADOS EN REGLAS, EFECTO ELIZA,
 * AUTOMATIZACIÓN CONTEXTUAL (SMART FEATURES) Y AUDITOR DE "AI WASHING"
 */
@Composable
fun HeuristicsElizaAndAiWashingCard(
    uiState: KdfUiState,
    onToggleSmartAutomation: (Boolean) -> Unit,
    onUpdateElizaQuery: (String) -> Unit,
    onExecuteElizaQuery: (String?) -> Unit,
    onRunFullHeuristicsAudit: () -> Unit
) {
    val isDark = uiState.isObsidianMode
    val eliza = uiState.lastElizaResponse

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Bimetallic3DIcon(
                    icon = Icons.Filled.Rule,
                    contentDescription = "Heurísticas, Efecto Eliza y Auditor AI Washing",
                    size = 42.dp,
                    goldOuterRing = true,
                    activeMewtwoGlow = uiState.smartAutomationEnabled,
                    isObsidianMode = isDark
                )
                Column {
                    MetallicGradientTitle(
                        text = "HEURÍSTICAS, EFECTO ELIZA, SMART FEATURES & AUDITOR AI WASHING",
                        fontSize = 13
                    )
                    Text(
                        text = "Reglas if/else (<1ms) • Sensores del SO • Transparencia Algorítmica Real",
                        style = MaterialTheme.typography.labelSmall,
                        color = HighGlossGoldHighlight
                    )
                }
            }
            JewelActionButton(
                text = "Auditar Sistema",
                icon = Icons.Filled.VerifiedUser,
                isPrimaryGold = true,
                isMewtwoAccent = true,
                isObsidianMode = isDark,
                onClick = onRunFullHeuristicsAudit,
                testTag = "btn_run_heuristics_ai_washing_audit"
            )
        }

        BimetallicDivider()

        // 1. Motor Interactivo de Heurísticas y "Efecto Eliza" (0 ms, Patrones Sintácticos y Reglas)
        Text(
            text = "1. Motor de Heurísticas Basadas en Reglas & Simulador 'Efecto Eliza' (0.1 ms sin Red):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = HighGlossGoldHighlight
        )
        Text(
            text = "Comprueba cómo las reglas condicionales (if/else + filtros estadísticos) y patrones sintácticos responden al instante adaptándose a los sensores del teléfono:",
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
        )

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "¿Hay corto en mi puerto USB?",
                "El teléfono está lento y con poca memoria RAM",
                "Mejorar imagen borrosa con GPU FSR",
                "Rescatar de bootloop por EDL 9008",
                "¿Cómo funciona el Efecto Eliza y AI Washing?"
            ).forEach { sample ->
                MetallicChipPill(
                    label = sample,
                    selected = false,
                    isObsidianMode = isDark,
                    onClick = { onExecuteElizaQuery(sample) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.elizaInputQuery,
                onValueChange = onUpdateElizaQuery,
                label = { Text("Consulta para motor de reglas / Efecto Eliza...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_eliza_heuristic"),
                singleLine = true
            )
            JewelActionButton(
                text = "Probar Regla",
                icon = Icons.Filled.Speed,
                isPrimaryGold = true,
                isObsidianMode = isDark,
                onClick = { onExecuteElizaQuery(null) },
                testTag = "btn_test_eliza_rule"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
                .border(1.dp, GoldMetallicBrush, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "⚡ PATRÓN ACTIVADO (${String.format(Locale.US, "%.2f", eliza.executionLatencyMs)} ms): ${eliza.matchedPatternRule}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = HighGlossGoldHighlight
                )
                Text(
                    text = "📊 ${eliza.statisticalFilterNote}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = MewtwoAuraGlow
                )
                Text(
                    text = eliza.elizaResponseText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.5.sp),
                    color = if (isDark) SpecularWhite else GraphiteCharcoalText
                )
            }
        }

        BimetallicDivider()

        // 2. Automatización Contextual / Smart Features (Sensores del Sistema Operativo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "2. Automatización Contextual / Smart Features (Sensores del SO):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = HighGlossGoldHighlight
                )
                Text(
                    text = "Usa batería, temperatura, hora, red y RAM para anticipar acciones de forma predictiva.",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                )
            }
            BimetallicPhysicalSwitch(
                checked = uiState.smartAutomationEnabled,
                onCheckedChange = onToggleSmartAutomation,
                testTag = "switch_smart_contextual_features"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            uiState.smartContextualRules.forEach { rule ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF141022) else PearlIvoryCanvas)
                        .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rule.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = HighGlossGoldHighlight
                            )
                            Text(
                                text = if (rule.isTriggered) "ACTIVA" else "EN ESPERA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (rule.isTriggered) TelemetrySafeEmerald else LiquidSilverLight
                            )
                        }
                        Text(
                            text = "Sensor: ${rule.sensorSource}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MewtwoAuraGlow
                        )
                        Text(
                            text = "Acción Predictiva: ${rule.predictedAction}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = if (isDark) SpecularWhite else GraphiteCharcoalText
                        )
                    }
                }
            }
        }

        BimetallicDivider()

        // 3. Auditor de "AI Washing" (Lavado de IA vs Hardware Nativo vs Reglas vs IA Neuronal Real)
        Text(
            text = "3. Auditor de 'AI Washing' (Transparencia Algorítmica de la Aplicación):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = HighGlossGoldHighlight
        )
        Text(
            text = "Desglose transparente de cada módulo para distinguir entre Hardware/ISP nativo, Heurísticas basadas en reglas e IA Neuronal real:",
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
        )

        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            uiState.aiTransparencyAuditItems.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
                        .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.featureName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = HighGlossGoldHighlight,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = item.verdictBadge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MewtwoAuraGlow
                            )
                        }
                        Text(
                            text = "Etiqueta comercial típica: ${item.marketingLabel} → Realidad técnica: ${item.realTechnicalImplementation}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isDark) SpecularWhite else GraphiteCharcoalText
                        )
                        Text(
                            text = "Composición Real: Hardware/SO=${item.hardwareOrOsPercent}% • Reglas/Heurística=${item.heuristicRulesPercent}% • IA Neuronal=${item.realNeuralAiPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.5.sp
                            ),
                            color = ImperialGoldPrimary
                        )
                    }
                }
            }
        }
    }
}
