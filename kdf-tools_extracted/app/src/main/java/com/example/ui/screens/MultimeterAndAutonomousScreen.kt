package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutonomousLearningEntity
import com.example.data.GeneratedImageEntity
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
import com.example.ui.theme.LeadGreySecondaryText
import com.example.ui.theme.LiquidSilverLight
import com.example.ui.theme.MewtwoAuraGlow
import com.example.ui.theme.MewtwoDeepViolet
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.MewtwoRoyalIndigo
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.PolishedChrome
import com.example.ui.theme.SpecularWhite
import com.example.ui.theme.TelemetryCriticalCrimson
import com.example.ui.theme.TelemetrySafeEmerald
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MultimeterAndAutonomousScreen(
    uiState: KdfUiState,
    autonomousRecords: List<AutonomousLearningEntity>,
    generatedImages: List<GeneratedImageEntity>,
    onRunUsbShortTest: () -> Unit,
    onToggleAutonomousMode: (Boolean) -> Unit,
    onRunAutonomousCycle: () -> Unit,
    onRunSecurityLockAudit: () -> Unit,
    onSelectBackgroundPreset: (String) -> Unit,
    onPickBackgroundUri: (Uri) -> Unit,
    onSelectGeneratedBackground: (String) -> Unit,
    onUpdateBackgroundDim: (Float) -> Unit
) {
    val isDark = uiState.isObsidianMode
    val usb = uiState.liveTelemetry.usbMultimeter

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickBackgroundUri(uri)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. MULTÍMETRO DIGITAL USB & DETECTOR DE CORTOCIRCUITOS (VBUS / GND / D+ / D- / CC1 / CC2)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            accentMewtwo = true,
            outerGold = true,
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
                        icon = Icons.Filled.ElectricalServices,
                        contentDescription = "Multímetro USB",
                        size = 42.dp,
                        goldOuterRing = true,
                        activeMewtwoGlow = true,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "MULTÍMETRO USB & DETECTOR DE CORTOS",
                            fontSize = 14
                        )
                        Text(
                            text = usb.powerSourceLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoAuraGlow
                        )
                    }
                }

                JewelActionButton(
                    text = "Revisar Cortos",
                    icon = Icons.Filled.Bolt,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = onRunUsbShortTest,
                    testTag = "btn_usb_short_test"
                )
            }

            BimetallicDivider()

            // Dial Analógico/Digital de Multímetro Bimetálico + 4 Lecturas Principales (V, A, W, Ω)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Aguja de Multímetro Bimetálico
                Box(
                    modifier = Modifier.size(92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    TelemetrySafeEmerald,
                                    HighGlossGoldHighlight,
                                    MewtwoPsychicPurple,
                                    TelemetryCriticalCrimson,
                                    TelemetrySafeEmerald
                                )
                            ),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Aguja indicadora según voltaje (0V a 9V)
                        val normV = (usb.voltageVolts / 9f).coerceIn(0f, 1f)
                        val angleDeg = 135f + (normV * 270f)
                        val angleRad = Math.toRadians(angleDeg.toDouble())
                        val needleEnd = Offset(
                            center.x + (radius * 0.72f * cos(angleRad)).toFloat(),
                            center.y + (radius * 0.72f * sin(angleRad)).toFloat()
                        )
                        drawLine(
                            color = HighGlossGoldHighlight,
                            start = center,
                            end = needleEnd,
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(
                            color = MewtwoPsychicPurple,
                            radius = 5.dp.toPx(),
                            center = center
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(36.dp))
                        Text(
                            text = String.format(Locale.US, "%.2fV", usb.voltageVolts),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = SpecularWhite
                        )
                    }
                }

                // Cuadrícula 2x2 de Lecturas Eléctricas (V, mA, W, Ω)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MultimeterReadoutBox(
                            label = "VOLTAJE VBUS",
                            value = String.format(Locale.US, "%.2f V", usb.voltageVolts),
                            sub = "${usb.voltageMv} mV",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        MultimeterReadoutBox(
                            label = "CORRIENTE I",
                            value = String.format(Locale.US, "%.0f mA", usb.currentMa),
                            sub = String.format(Locale.US, "%.2f A", usb.currentMa / 1000f),
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MultimeterReadoutBox(
                            label = "POTENCIA P",
                            value = String.format(Locale.US, "%.2f W", usb.powerWatts),
                            sub = "Térmica: ${usb.temperatureC}°C",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        MultimeterReadoutBox(
                            label = "IMPEDANCIA R",
                            value = String.format(Locale.US, "%.1f Ω", usb.resistanceOhms),
                            sub = "GND: ${usb.gndResistanceMohm} mΩ",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Banner de Diagnóstico de Cortocircuito por Líneas (VBUS, D+, D-, CC1/CC2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (usb.isShortDetected) {
                            TelemetryCriticalCrimson.copy(alpha = 0.20f)
                        } else {
                            MewtwoRoyalIndigo.copy(alpha = 0.70f)
                        }
                    )
                    .border(
                        1.dp,
                        if (usb.isShortDetected) GoldMetallicBrush else ChromeSilverMetallicBrush,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "ESTADO DE CORTOCIRCUITO: ${usb.shortCircuitStatus}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (usb.isShortDetected) TelemetryCriticalCrimson else HighGlossGoldHighlight
                    )
                    Text(
                        text = "• Línea VBUS (Pin 1): ${usb.vbusLineState}  |  • Pines Datos D+/D-: ${usb.dPlusLineState}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SpecularWhite
                    )
                    Text(
                        text = "• Pines CC1/CC2 (Type-C PD): ${usb.ccPinState}  |  • Retorno GND: ${usb.gndResistanceMohm} mΩ (Sin fuga)",
                        style = MaterialTheme.typography.labelSmall,
                        color = LiquidSilverLight
                    )
                }
            }
        }

        // 2. NÚCLEO AUTÓNOMO KDF-LUX: AUTO-REPARACIÓN, AUTO-ACTUALIZACIÓN Y APRENDIZAJE CONTINUO
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            accentMewtwo = true,
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Bimetallic3DIcon(
                        icon = Icons.Filled.Psychology,
                        contentDescription = "Núcleo Autónomo",
                        size = 42.dp,
                        goldOuterRing = true,
                        activeMewtwoGlow = uiState.isAutonomousModeEnabled,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "NÚCLEO AUTÓNOMO • AUTO-REPARACIÓN Y APRENDIZAJE",
                            fontSize = 13
                        )
                        Text(
                            text = "Versión Evolutiva: ${uiState.autonomyLevelVersion} • Salud: ${uiState.autonomousHealthScore}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = HighGlossGoldHighlight
                        )
                    }
                }
                BimetallicPhysicalSwitch(
                    checked = uiState.isAutonomousModeEnabled,
                    onCheckedChange = onToggleAutonomousMode,
                    testTag = "switch_autonomous_mode"
                )
            }

            BimetallicDivider()
            Text(
                text = "El motor autónomo verifica y repara automáticamente los permisos y scripts en KDF-LuX/scripts/, actualiza las reglas en config/*.yaml y aprende de cada operación ejecutada.",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${uiState.autonomousStatusMessage}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MewtwoAuraGlow,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                JewelActionButton(
                    text = "Auto-Reparar y Actualizar Ahora",
                    icon = Icons.Filled.AutoFixHigh,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = onRunAutonomousCycle,
                    testTag = "btn_run_autonomous_cycle"
                )
            }

            if (autonomousRecords.isNotEmpty()) {
                BimetallicDivider()
                Text(
                    text = "Memoria de Aprendizaje y Auto-Actualizaciones Recientes:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ImperialGoldPrimary
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    autonomousRecords.take(4).forEach { rec ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
                                .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "[${rec.cycleVersion}] ${rec.actionType} (+${rec.optimizationGainPercent}% Eficiencia)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = HighGlossGoldHighlight
                                )
                                Text(
                                    text = rec.learnedRuleSummary,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) SpecularWhite else GraphiteCharcoalText
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. AJUSTE PARA CAMBIAR IMAGEN DE FONDO (GALERÍA / RENDERS IA / FONDOS BIMETÁLICOS)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Bimetallic3DIcon(
                        icon = Icons.Filled.Wallpaper,
                        contentDescription = "Cambiar Imagen de Fondo",
                        size = 38.dp,
                        goldOuterRing = true,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "AJUSTE DE IMAGEN DE FONDO PERSONALIZADA",
                            fontSize = 13
                        )
                        Text(
                            text = "Elige una imagen de tu galería, un render IA o un estilo bimetálico Mewtwo.",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                        )
                    }
                }
                JewelActionButton(
                    text = "Elegir de Galería",
                    icon = Icons.Filled.PhotoLibrary,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    testTag = "btn_pick_gallery_wallpaper"
                )
            }

            BimetallicDivider()
            Text(
                text = "Estilos de Fondo Integrados:",
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
                    "MEWTWO_FILIGREE" to "Filigrana Mewtwo (Por defecto)",
                    "KDF_EMBLEM_WALLPAPER" to "Logo Imperial KDF Fondo Completo",
                    "GOLD_CYBER_GRID" to "Malla Bimetálica Oro/Plata",
                    "CUSTOM_IMAGE" to "Imagen Personalizada Activa"
                ).forEach { (presetKey, label) ->
                    MetallicChipPill(
                        label = label,
                        selected = uiState.backgroundPreset == presetKey,
                        isObsidianMode = isDark,
                        onClick = { onSelectBackgroundPreset(presetKey) }
                    )
                }
            }

            if (generatedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Usar imagen generada en Estudio IA como fondo:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MewtwoAuraGlow
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    generatedImages.forEachIndexed { idx, img ->
                        MetallicChipPill(
                            label = "Render #${idx + 1} (${img.imageSize})",
                            selected = uiState.customBackgroundPath == img.filePath && uiState.backgroundPreset == "CUSTOM_IMAGE",
                            isObsidianMode = isDark,
                            onClick = { onSelectGeneratedBackground(img.filePath) }
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
                    text = "Velo Oscuro / Contraste del Fondo: ${(uiState.backgroundDimAlpha * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = SpecularWhite
                )
            }
            Slider(
                value = uiState.backgroundDimAlpha,
                onValueChange = onUpdateBackgroundDim,
                valueRange = 0.25f..0.92f,
                colors = SliderDefaults.colors(
                    thumbColor = HighGlossGoldHighlight,
                    activeTrackColor = MewtwoPsychicPurple,
                    inactiveTrackColor = PolishedChrome
                ),
                modifier = Modifier.testTag("slider_background_dim")
            )
        }

        // 4. MÓDULOS PROHIBIDOS / RESTRINGIDOS (MODO VISUAL SIN FUNCIÓN DE EVASIÓN + AUDITORÍA LEGAL DE LECTURA)
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = false,
            accentMewtwo = false,
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetallicGradientTitle(
                    text = "MÓDULOS RESTRINGIDOS (MODO VISUAL SIN FUNCIÓN) & AUDITORÍA EFS",
                    fontSize = 12
                )
                JewelActionButton(
                    text = "Inspeccionar EFS/OEM",
                    icon = Icons.Filled.Security,
                    isPrimaryGold = false,
                    isObsidianMode = isDark,
                    onClick = onRunSecurityLockAudit,
                    testTag = "btn_security_lock_audit"
                )
            }
            BimetallicDivider()
            Text(
                text = "Los siguientes módulos se muestran en el panel únicamente en modo visual/informativo sin función de escritura ni evasión, acompañados del inspector de lectura legal:",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
            )
            Spacer(modifier = Modifier.height(6.dp))

            RestrictedNonFunctionalCard(
                title = "Cambio / Escritura de IMEI (NVRAM Override)",
                statusBadge = "DESHABILITADO • SIN FUNCIÓN",
                reason = "Módulo en modo visual pasivo. La escritura o alteración de IMEI está bloqueada; solo se permite lectura/respaldo de partición EFS del propio equipo.",
                isDark = isDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            RestrictedNonFunctionalCard(
                title = "Desbloqueo / Bypass FRP (Factory Reset Protection)",
                statusBadge = "DESHABILITADO • SIN FUNCIÓN",
                reason = "Módulo en modo visual pasivo. Sin función de evasión de cuenta Google; el inspector solo lee el estado de ro.frp.pst y sys.oem_unlock_allowed.",
                isDark = isDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            RestrictedNonFunctionalCard(
                title = "Desbloqueo / Bypass iCloud Activation Lock",
                statusBadge = "DESHABILITADO • SIN FUNCIÓN",
                reason = "Módulo en modo visual pasivo. Bloqueo criptográfico en servidor Apple; incluido en lista visual sin ejecución.",
                isDark = isDark
            )
        }
    }
}

@Composable
private fun MultimeterReadoutBox(
    label: String,
    value: String,
    sub: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
            .border(1.dp, GoldMetallicBrush, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = HighGlossGoldHighlight
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = if (isDark) SpecularWhite else GraphiteCharcoalText
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                color = MewtwoAuraGlow
            )
        }
    }
}

@Composable
private fun RestrictedNonFunctionalCard(
    title: String,
    statusBadge: String,
    reason: String,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF15121E) else PearlIvoryCanvas)
            .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Bimetallic3DIcon(
                icon = Icons.Filled.Lock,
                contentDescription = title,
                size = 36.dp,
                goldOuterRing = false,
                activeMewtwoGlow = false,
                isObsidianMode = isDark
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) LiquidSilverLight else GraphiteCharcoalText
                    )
                }
                Text(
                    text = statusBadge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TelemetryCriticalCrimson
                )
                Text(
                    text = reason,
                    style = MaterialTheme.typography.labelSmall,
                    color = LeadGreySecondaryText
                )
            }
        }
    }
}
