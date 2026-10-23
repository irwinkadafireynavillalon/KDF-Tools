package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BimetallicCardSurface
import com.example.ui.components.MetallicGradientTitle
import com.example.ui.theme.DeepGoldShadow
import com.example.ui.theme.GoldMetallicBrush
import com.example.ui.theme.HighGlossGoldHighlight
import com.example.ui.theme.ImperialGoldPrimary
import com.example.ui.theme.LiquidSilverLight
import com.example.ui.theme.MewtwoAuraGlow
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.MewtwoRoyalIndigo
import com.example.ui.theme.PureCeramicWhite
import com.example.ui.theme.TelemetrySafeEmerald
import com.example.ui.theme.TelemetryWarningAmber

data class OrchestratorPlanStep(
    val id: String,
    val stepNumber: Int,
    val title: String,
    val actionScript: String,
    val description: String,
    var status: String = "PENDIENTE" // "PENDIENTE", "EJECUTANDO", "COMPLETADO"
)

/**
 * Pantalla de Asistente Personal & Planificador del Orquestador KDF-Tools
 * Conversa con el orquestador (Spark Core + Gemini), crea planes técnicos paso a paso
 * y permite disparar la ejecución de scripts directamente sobre el hardware.
 */
@Composable
fun PersonalAssistantAndPlannerScreen(
    isObsidianMode: Boolean,
    onExecuteStep: (String) -> Unit = {},
    onConsultAi: (String) -> Unit = {}
) {
    var assistantInput by remember { mutableStateOf("") }
    var selectedPlanPreset by remember { mutableStateOf("PAYJOY_REMOVAL") }
    var isExecutingStepId by remember { mutableStateOf<String?>(null) }

    var planSteps by remember {
        mutableStateOf(
            listOf(
                OrchestratorPlanStep(
                    id = "step_1",
                    stepNumber = 1,
                    title = "Activación de Modo Seguro Aislado",
                    actionScript = "scripts/enter_safe_mode.sh",
                    description = "Aplica persist.sys.safemode=1 para inmovilizar la app bloqueadora.",
                    status = "COMPLETADO"
                ),
                OrchestratorPlanStep(
                    id = "step_2",
                    stepNumber = 2,
                    title = "Desactivación del Paquete Bloqueador",
                    actionScript = "scripts/disable_blocker.sh com.ejemplo.blocker disable no",
                    description = "Ejecuta pm disable-user y purga almacenamiento temporal del paquete.",
                    status = "PENDIENTE"
                ),
                OrchestratorPlanStep(
                    id = "step_3",
                    stepNumber = 3,
                    title = "Limpieza de Particiones y Caché Dalvik",
                    actionScript = "scripts/clean_memory.sh no",
                    description = "Purga /data/cache/* y /cache/* para optimizar memoria RAM.",
                    status = "PENDIENTE"
                ),
                OrchestratorPlanStep(
                    id = "step_4",
                    stepNumber = 4,
                    title = "Auditoría de Seguridad y Retorno Seguro",
                    actionScript = "scripts/return_home.sh",
                    description = "Verifica integridad del arranque y reinicia el dispositivo en modo normal.",
                    status = "PENDIENTE"
                )
            )
        )
    }

    var assistantDialogHistory by remember {
        mutableStateOf(
            listOf(
                "Asistente" to "¡Hola! Soy tu Asistente Personal y Planificador del Orquestador KDF-Tools. Tengo acceso al núcleo Spark y Gemini 3.8 Flash para coordinar acciones con el backend. ¿Qué objetivo técnico deseas planificar hoy?"
            )
        )
    }

    fun applyPlanPreset(presetId: String) {
        selectedPlanPreset = presetId
        planSteps = when (presetId) {
            "UNBRICK" -> listOf(
                OrchestratorPlanStep("u1", 1, "Diagnóstico de Bajo Nivel EDL / Fastboot", "scripts/low_level_diagnostic.sh", "Detecta estado de puertos y procesador."),
                OrchestratorPlanStep("u2", 2, "Desbloqueo de Bootloader", "scripts/unlock_bootloader.sh", "Comprueba permisos OEM y desbloquea."),
                OrchestratorPlanStep("u3", 3, "Rescate de Partición Boot & Recovery", "scripts/unbrick_recovery.sh", "Escribe vbmeta deshabilitado y boot seguro."),
                OrchestratorPlanStep("u4", 4, "Reinicio y Verificación de Estado", "scripts/return_home.sh", "Comprueba que Android inicie sin bootloop.")
            )
            "MEMORY_CLEAN" -> listOf(
                OrchestratorPlanStep("m1", 1, "Inspección de Consumo de RAM y Cachés", "scripts/clean_memory.sh", "Analiza /proc/meminfo y espacio libre."),
                OrchestratorPlanStep("m2", 2, "Purga de Particiones Temporales", "scripts/clean_memory.sh", "Limpia /data/cache/* y ejecuta fstrim."),
                OrchestratorPlanStep("m3", 3, "Optimización de Procesos en Segundo Plano", "scripts/enter_safe_mode.sh --off", "Restaura estado óptimo.")
            )
            "EDL_FLASH" -> listOf(
                OrchestratorPlanStep("e1", 1, "Detección de Puerto Qualcomm QDLoader 9008", "scripts/low_level_diagnostic.sh", "Verifica conexión USB test-point."),
                OrchestratorPlanStep("e2", 2, "Carga de Programador Sahara Firehose", "scripts/edl_flasher.sh bootloader.img radio.img prog_firehose_ddr.elf", "Escribe bootloader y módem por puerto de emergencia."),
                OrchestratorPlanStep("e3", 3, "Reset de Procesador y Arranque", "scripts/return_home.sh", "Reinicia el SoC Snapdragon.")
            )
            else -> listOf(
                OrchestratorPlanStep("p1", 1, "Activación de Modo Seguro Aislado", "scripts/enter_safe_mode.sh", "Aplica persist.sys.safemode=1 para inmovilizar la app bloqueadora.", "COMPLETADO"),
                OrchestratorPlanStep("p2", 2, "Desactivación del Paquete Bloqueador", "scripts/disable_blocker.sh com.ejemplo.blocker disable no", "Ejecuta pm disable-user y purga almacenamiento temporal del paquete."),
                OrchestratorPlanStep("p3", 3, "Limpieza de Particiones y Caché Dalvik", "scripts/clean_memory.sh no", "Purga /data/cache/* y /cache/* para optimizar memoria RAM."),
                OrchestratorPlanStep("p4", 4, "Auditoría de Seguridad y Retorno Seguro", "scripts/return_home.sh", "Verifica integridad del arranque y reinicia el dispositivo.")
            )
        }
        assistantDialogHistory = assistantDialogHistory + ("Asistente" to "He generado y cargado en el Orquestador el plan: **$presetId** con ${planSteps.size} fases secuenciales.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cabecera: Asistente & Estado del Orquestador
        BimetallicCardSurface(
            isObsidianMode = isObsidianMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Asistente",
                            tint = HighGlossGoldHighlight,
                            modifier = Modifier.size(18.dp)
                        )
                        MetallicGradientTitle(
                            text = "ASISTENTE PERSONAL & ORQUESTADOR",
                            fontSize = 14
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TelemetrySafeEmerald)
                        )
                        Text(
                            text = "ORQUESTADOR CONECTADO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TelemetrySafeEmerald
                        )
                    }
                }

                Text(
                    text = "El Asistente coordina el motor Spark Heuristic + Gemini 3.8 Flash con los scripts del sistema para planificar y ejecutar rescates.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = LiquidSilverLight.copy(alpha = 0.7f)
                )

                // Selector de Planes Predeterminados
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "PAYJOY_REMOVAL" to "🚫 Plan App Blocker",
                        "UNBRICK" to "🔧 Plan Rescate Bootloop",
                        "MEMORY_CLEAN" to "🧹 Plan Limpieza RAM",
                        "EDL_FLASH" to "⚡ Plan Flasheo EDL 9008"
                    ).forEach { (presetId, label) ->
                        val isSelected = selectedPlanPreset == presetId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MewtwoPsychicPurple else MewtwoRoyalIndigo.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ImperialGoldPrimary else MewtwoPsychicPurple.copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { applyPlanPreset(presetId) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected) PureCeramicWhite else LiquidSilverLight
                            )
                        }
                    }
                }
            }
        }

        // Tablero del Planificador (Pasos del Plan Activo)
        Text(
            text = "PASOS DEL PLAN ACTIVO EN EL ORQUESTADOR",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = HighGlossGoldHighlight
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(planSteps) { step ->
                val isCompleted = step.status == "COMPLETADO"
                val isRunning = isExecutingStepId == step.id

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MewtwoObsidianBlack)
                        .border(
                            1.dp,
                            if (isCompleted) TelemetrySafeEmerald else if (isRunning) ImperialGoldPrimary else MewtwoPsychicPurple.copy(alpha = 0.4f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) TelemetrySafeEmerald else MewtwoRoyalIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "OK",
                                        tint = PureCeramicWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${step.stepNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = PureCeramicWhite
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = PureCeramicWhite
                                )
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = LiquidSilverLight.copy(alpha = 0.65f)
                                )
                                Text(
                                    text = "Comando: ${step.actionScript}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = ImperialGoldPrimary
                                )
                            }
                        }

                        // Botón Ejecutar Paso
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isCompleted) SolidColor(MewtwoRoyalIndigo.copy(alpha = 0.5f)) else GoldMetallicBrush
                                )
                                .clickable(enabled = !isCompleted && !isRunning) {
                                    isExecutingStepId = step.id
                                    onExecuteStep(step.actionScript)
                                    step.status = "COMPLETADO"
                                    isExecutingStepId = null
                                    assistantDialogHistory = assistantDialogHistory + ("Orquestador" to "Paso #${step.stepNumber} ejecutado: ${step.title}.")
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = MewtwoObsidianBlack,
                                    strokeWidth = 1.5.dp
                                )
                            } else {
                                Text(
                                    text = if (isCompleted) "Listo" else "Ejecutar",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isCompleted) LiquidSilverLight.copy(alpha = 0.5f) else MewtwoObsidianBlack
                                )
                            }
                        }
                    }
                }
            }
        }

        // Caja de Diálogo con el Asistente Personal
        BimetallicCardSurface(
            isObsidianMode = isObsidianMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CANAL DIRECTO CON EL ORQUESTADOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MewtwoAuraGlow
                )

                // Historial breve
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(MewtwoObsidianBlack, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    assistantDialogHistory.takeLast(2).forEach { (speaker, text) ->
                        Text(
                            text = "$speaker: $text",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (speaker == "Asistente") MewtwoAuraGlow else PureCeramicWhite,
                            maxLines = 2
                        )
                    }
                }

                // Input para planificar con lenguaje natural
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = assistantInput,
                        onValueChange = { assistantInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        singleLine = true,
                        placeholder = {
                            Text(
                                "Pide planificar o ejecutar algo al orquestador...",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = LiquidSilverLight.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImperialGoldPrimary,
                            unfocusedBorderColor = MewtwoPsychicPurple.copy(alpha = 0.5f),
                            focusedContainerColor = MewtwoObsidianBlack,
                            unfocusedContainerColor = MewtwoObsidianBlack
                        ),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = PureCeramicWhite
                        )
                    )

                    IconButton(
                        onClick = {
                            val msg = assistantInput.trim()
                            if (msg.isNotEmpty()) {
                                assistantDialogHistory = assistantDialogHistory + ("Tú" to msg)
                                onConsultAi(msg)
                                assistantInput = ""
                                // Respuesta instantánea heurística Spark
                                assistantDialogHistory = assistantDialogHistory + ("Asistente" to "Analizando con el Orquestador Spark. Pasos añadidos a la cola de planificación.")
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldMetallicBrush)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = MewtwoObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
