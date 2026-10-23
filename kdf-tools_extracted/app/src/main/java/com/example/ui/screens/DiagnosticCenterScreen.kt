package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.theme.MewtwoAuraGlow
import com.example.ui.theme.MewtwoDeepViolet
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.MewtwoRoyalIndigo
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.SpecularWhite
import com.example.ui.theme.TelemetryCriticalCrimson
import com.example.ui.theme.TelemetrySafeEmerald
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interfaz de Diagnóstico Unificada KDF-LuX
 * Reúne exactamente:
 * 1. Funciones de Conectividad (ADB Inalámbrico, USB Host/OTG, Wi-Fi, Ethernet, Fastboot, EDL)
 * 2. Descripción de Problema (Selector de síntomas comunes + descripción del usuario + auto-análisis)
 * 3. Estado del Dispositivo (Telemetría viva de SoC, RAM, Batería, Almacenamiento, Bootloader)
 * 4. Multímetro Local (VBUS, Corriente, Potencia, Resistencia, D+/D-, CC1/CC2, Detector de cortos)
 * 5. El Chatbot KDF-LuX Integrado (con modo híbrido, comandos directos y diagnóstico en tiempo real)
 */
@Composable
fun DiagnosticCenterScreen(
    uiState: KdfUiState,
    onTestConnectivity: () -> Unit,
    onSelectProblemType: (String) -> Unit,
    onUpdateProblemDescription: (String) -> Unit,
    onAnalyzeProblem: () -> Unit,
    onRunUsbShortTest: () -> Unit,
    onSelectChatbotMode: (String) -> Unit,
    onSendChatMessage: (String) -> Unit,
    onOpenFullChatModal: () -> Unit
) {
    val isDark = uiState.isObsidianMode
    val usb = uiState.liveTelemetry.usbMultimeter
    val hw = uiState.liveTelemetry

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // 1. FUNCIONES DE CONECTIVIDAD
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
                        icon = Icons.Filled.Router,
                        contentDescription = "Conectividad",
                        size = 38.dp,
                        goldOuterRing = true,
                        activeMewtwoGlow = true,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "FUNCIONES DE CONECTIVIDAD",
                            fontSize = 14
                        )
                        Text(
                            text = "Puente ADB, USB Host/OTG, Wi-Fi LAN y Protocolos de Arranque",
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoAuraGlow
                        )
                    }
                }
                JewelActionButton(
                    text = if (uiState.isTestingConnectivity) "Probando..." else "Probar Red/ADB",
                    icon = Icons.Filled.NetworkCheck,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = onTestConnectivity,
                    testTag = "btn_test_connectivity"
                )
            }

            BimetallicDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectivityStatusPill(
                    label = "ADB INALÁMBRICO",
                    status = uiState.wirelessAdbEndpoint,
                    subtext = "Socket puerto 5555",
                    isActive = true,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ConnectivityStatusPill(
                    label = "USB HOST / OTG",
                    status = "${hw.usbDevicesCount} Disp. OTG",
                    subtext = usb.powerSourceLabel,
                    isActive = hw.usbDevicesCount > 0 || usb.voltageVolts > 4.5f,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ConnectivityStatusPill(
                    label = "MODO PROTOCOLO",
                    status = hw.bootloaderVersion.take(12),
                    subtext = "Fastboot / EDL 9008",
                    isActive = true,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
                    .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "• Estado Conectividad: ${uiState.lastConnectivityResult}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = TelemetrySafeEmerald
                )
            }
        }

        // 2. DESCRIPCIÓN DEL PROBLEMA
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
                        icon = Icons.Filled.ReportProblem,
                        contentDescription = "Problema",
                        size = 38.dp,
                        goldOuterRing = false,
                        activeMewtwoGlow = true,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "DESCRIPCIÓN DEL PROBLEMA",
                            fontSize = 14
                        )
                        Text(
                            text = "Selecciona o detalla el síntoma técnico para evaluar causa y script",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                        )
                    }
                }
                JewelActionButton(
                    text = "Diagnosticar con IA",
                    icon = Icons.Filled.Psychology,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = onAnalyzeProblem,
                    testTag = "btn_analyze_problem"
                )
            }

            BimetallicDivider()

            Text(
                text = "Problemas frecuentes detectados:",
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
                    "Bootloop / Reinicio en logo",
                    "Caída de voltaje / Corto en puerto USB",
                    "App bloqueadora / Bloqueo en inicio",
                    "Memoria saturada / Congelamientos",
                    "Dispositivo brickeado (EDL 9008)",
                    "Bloqueo OEM / FRP / EFS dañado"
                ).forEach { probType ->
                    MetallicChipPill(
                        label = probType,
                        selected = uiState.selectedDiagnosticProblemType == probType,
                        isObsidianMode = isDark,
                        onClick = { onSelectProblemType(probType) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = uiState.diagnosticProblemDescription,
                onValueChange = onUpdateProblemDescription,
                label = { Text("Detalla el síntoma del dispositivo...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_problem_description"),
                maxLines = 3
            )
        }

        // 3. ESTADO DEL DISPOSITIVO
        BimetallicCeramicCard(
            isObsidianMode = isDark,
            outerGold = true,
            accentMewtwo = false,
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Bimetallic3DIcon(
                    icon = Icons.Filled.PermDeviceInformation,
                    contentDescription = "Estado del Dispositivo",
                    size = 38.dp,
                    goldOuterRing = true,
                    isObsidianMode = isDark
                )
                Column {
                    MetallicGradientTitle(
                        text = "ESTADO DEL DISPOSITIVO",
                        fontSize = 14
                    )
                    Text(
                        text = "${hw.deviceModel} • Android ${hw.androidVersion} (SDK ${hw.sdkInt})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MewtwoAuraGlow
                    )
                }
            }

            BimetallicDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoTelemetryCell(
                    label = "PROCESADOR / SOC",
                    value = hw.socHardware.take(18),
                    sub = "${Runtime.getRuntime().availableProcessors()} Hilos activos",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                InfoTelemetryCell(
                    label = "MEMORIA RAM",
                    value = "${hw.ramUsedPercent}% en uso",
                    sub = "${hw.ramAvailableMb} MB libres",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                InfoTelemetryCell(
                    label = "BATERÍA Y TEMP",
                    value = "${hw.batteryLevel}% (${hw.batteryVoltageMv}mV)",
                    sub = "${hw.batteryTempC}°C",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoTelemetryCell(
                    label = "ALMACENAMIENTO",
                    value = "${hw.storageFreeGb} libres",
                    sub = "Total: ${hw.storageTotalGb}",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                InfoTelemetryCell(
                    label = "BOOTLOADER / AVB",
                    value = hw.bootloaderVersion.take(14),
                    sub = hw.verifiedBootState.take(14),
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                InfoTelemetryCell(
                    label = "SALUD DEL SISTEMA",
                    value = "${uiState.autonomousHealthScore}% OK",
                    sub = uiState.autonomyLevelVersion,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. MULTÍMETRO LOCAL (MEDICIÓN ELÉCTRICA EN VIVO & CORTOCIRCUITOS)
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
                        icon = Icons.Filled.ElectricalServices,
                        contentDescription = "Multímetro Local",
                        size = 38.dp,
                        goldOuterRing = true,
                        activeMewtwoGlow = true,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "MULTÍMETRO LOCAL (TYPE-C / USB)",
                            fontSize = 14
                        )
                        Text(
                            text = "Voltaje VBUS, Corriente, Potencia, Resistencia y Pines D+/D-/CC",
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoAuraGlow
                        )
                    }
                }
                JewelActionButton(
                    text = "Escanear Corto",
                    icon = Icons.Filled.Bolt,
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = isDark,
                    onClick = onRunUsbShortTest,
                    testTag = "btn_diag_short_test"
                )
            }

            BimetallicDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dial Analógico/Digital
                Box(
                    modifier = Modifier.size(86.dp),
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
                            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                        )

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
                            radius = 4.dp.toPx(),
                            center = center
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(30.dp))
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

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoTelemetryCell(
                            label = "VOLTAJE VBUS",
                            value = String.format(Locale.US, "%.2f V", usb.voltageVolts),
                            sub = "${usb.voltageMv} mV",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        InfoTelemetryCell(
                            label = "CORRIENTE I",
                            value = String.format(Locale.US, "%.0f mA", usb.currentMa),
                            sub = String.format(Locale.US, "%.2f A", usb.currentMa / 1000f),
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoTelemetryCell(
                            label = "POTENCIA P",
                            value = String.format(Locale.US, "%.2f W", usb.powerWatts),
                            sub = "Temp: ${usb.temperatureC}°C",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        InfoTelemetryCell(
                            label = "RESISTENCIA R",
                            value = String.format(Locale.US, "%.1f Ω", usb.resistanceOhms),
                            sub = "GND: ${usb.gndResistanceMohm}mΩ",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (usb.isShortDetected) TelemetryCriticalCrimson.copy(alpha = 0.20f)
                        else MewtwoRoyalIndigo.copy(alpha = 0.70f)
                    )
                    .border(1.dp, if (usb.isShortDetected) GoldMetallicBrush else ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "DIAGNÓSTICO ELÉCTRICO: ${usb.shortCircuitStatus}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (usb.isShortDetected) TelemetryCriticalCrimson else HighGlossGoldHighlight
                    )
                    Text(
                        text = "• Líneas: VBUS (${usb.vbusLineState}) | Datos (${usb.dPlusLineState}) | Type-C CC (${usb.ccPinState})",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = SpecularWhite
                    )
                }
            }
        }

        // 5. EL CHATBOT KDF-LUX INTEGRADO DE FORMA NATIVA EN EL DIAGNÓSTICO
        KdfInteractiveChatbotCard(
            uiState = uiState,
            onSelectMode = onSelectChatbotMode,
            onSendMessage = onSendChatMessage,
            onOpenFullModal = onOpenFullChatModal
        )
    }
}

@Composable
private fun ConnectivityStatusPill(
    label: String,
    status: String,
    subtext: String,
    isActive: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) MewtwoObsidianBlack else PearlIvoryCanvas)
            .border(
                1.dp,
                if (isActive) GoldMetallicBrush else ChromeSilverMetallicBrush,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = HighGlossGoldHighlight
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = if (isDark) SpecularWhite else GraphiteCharcoalText
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = MewtwoAuraGlow
            )
        }
    }
}

@Composable
private fun InfoTelemetryCell(
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
            .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
            .padding(7.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = HighGlossGoldHighlight
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (isDark) SpecularWhite else GraphiteCharcoalText
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = LiquidSilverLight
            )
        }
    }
}
