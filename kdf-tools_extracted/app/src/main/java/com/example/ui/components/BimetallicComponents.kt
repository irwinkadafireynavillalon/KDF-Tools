package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.BrushedSteelShadow
import com.example.ui.theme.ChromeSilverMetallicBrush
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.DamaskFiligreeLight
import com.example.ui.theme.DeepGoldShadow
import com.example.ui.theme.DividerBimetallicBrush
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * 1. Fondo General Oscuro Bimetálico con Filigrana / Textura Adamascada y Aura Psíquica Mewtwo
 * Integra de forma coherente Negro Obsidiana, Oro, Plata, Blanco y Morado Mewtwo.
 */
@Composable
fun DamaskFiligreeBackground(
    isObsidianMode: Boolean,
    customBackgroundPath: String? = null,
    backgroundPreset: String = "MEWTWO_FILIGREE",
    backgroundDimAlpha: Float = 0.72f,
    backgroundBlurRadius: Float = 0f,
    visualContrastBoost: Float = 1.15f,
    computationalHdrActive: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bgTop = when (backgroundPreset) {
        "PURPLE_NEBULA" -> Color(0xFF1E0E38)
        "GOLD_CYBER_GRID" -> Color(0xFF1A150A)
        "ROYAL_SHIELD" -> Color(0xFF121424)
        else -> if (isObsidianMode) MewtwoObsidianBlack else PureCeramicWhite
    }
    val bgMid = when (backgroundPreset) {
        "PURPLE_NEBULA" -> Color(0xFF2D1654)
        "GOLD_CYBER_GRID" -> Color(0xFF281F0D)
        "ROYAL_SHIELD" -> Color(0xFF1B1E34)
        else -> if (isObsidianMode) Color(0xFF140F23) else PearlIvoryCanvas
    }
    val bgBottom = when (backgroundPreset) {
        "PURPLE_NEBULA" -> Color(0xFF0C0517)
        "GOLD_CYBER_GRID" -> Color(0xFF0F0B05)
        "ROYAL_SHIELD" -> Color(0xFF0B0D18)
        else -> if (isObsidianMode) Color(0xFF09070F) else PearlIvoryCanvas
    }

    val filigreeGold = if (isObsidianMode) {
        ImperialGoldPrimary.copy(alpha = if (backgroundPreset == "GOLD_CYBER_GRID") 0.22f else 0.08f)
    } else {
        DamaskFiligreeLight.copy(alpha = 0.65f)
    }
    val filigreeSilver = if (isObsidianMode) {
        PolishedChrome.copy(alpha = if (backgroundPreset == "ROYAL_SHIELD") 0.18f else 0.06f)
    } else {
        PolishedChrome.copy(alpha = 0.35f)
    }
    val psychicHalo = if (isObsidianMode) {
        MewtwoPsychicPurple.copy(alpha = if (backgroundPreset == "PURPLE_NEBULA") 0.40f else 0.20f)
    } else {
        MewtwoPsychicPurple.copy(alpha = 0.06f)
    }

    val customBitmap = androidx.compose.runtime.remember(customBackgroundPath) {
        if (!customBackgroundPath.isNullOrBlank()) {
            val f = java.io.File(customBackgroundPath)
            if (f.exists()) android.graphics.BitmapFactory.decodeFile(f.absolutePath) else null
        } else {
            null
        }
    }

    val gpuColorFilter = androidx.compose.runtime.remember(visualContrastBoost, computationalHdrActive) {
        val c = visualContrastBoost.coerceIn(0.8f, 1.6f) * (if (computationalHdrActive) 1.08f else 1.0f)
        val t = (-0.5f * c + 0.5f) * 255f
        val matrix = androidx.compose.ui.graphics.ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, t,
                0f, c, 0f, 0f, t,
                0f, 0f, c * 1.04f, 0f, t + 4f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        androidx.compose.ui.graphics.ColorFilter.colorMatrix(matrix)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgBottom)))
    ) {
        // Imagen personalizada o preset especial
        if (customBitmap != null && backgroundPreset == "CUSTOM_IMAGE") {
            Image(
                bitmap = customBitmap.asImageBitmap(),
                contentDescription = "Imagen de Fondo Personalizada KDF-Tools",
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (backgroundBlurRadius > 0.5f) Modifier.padding((backgroundBlurRadius * 0.1f).dp) else Modifier),
                contentScale = ContentScale.Crop,
                colorFilter = gpuColorFilter
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MewtwoObsidianBlack.copy(alpha = backgroundDimAlpha.coerceIn(0.10f, 0.95f)))
            )
        } else if (backgroundPreset == "KDF_EMBLEM_WALLPAPER") {
            Image(
                painter = painterResource(id = R.drawable.img_kdf_logo),
                contentDescription = "Fondo Emblema KDF-Tools",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = gpuColorFilter
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MewtwoObsidianBlack.copy(alpha = backgroundDimAlpha.coerceIn(0.20f, 0.95f)))
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 46.dp.toPx()
            val cols = (size.width / step).toInt() + 1
            val rows = (size.height / step).toInt() + 1

            // Halo radial psíquico morado Mewtwo superior
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(psychicHalo, MewtwoDeepViolet.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.18f),
                    radius = size.width * 0.72f
                ),
                radius = size.width * 0.72f,
                center = Offset(size.width * 0.5f, size.height * 0.18f)
            )

            // Segundo destello dorado/plateado inferior
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ImperialGoldPrimary.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.78f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.85f, size.height * 0.78f)
            )

            // Filigrana geométrica bimetálica en Oro y Plata
            for (r in 0..rows) {
                for (c in 0..cols) {
                    val cx = c * step + if (r % 2 == 0) 0f else step / 2f
                    val cy = r * step
                    drawCircle(
                        color = if ((r + c) % 2 == 0) filigreeGold else filigreeSilver,
                        radius = step * 0.27f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1f)
                    )
                    if (backgroundPreset == "GOLD_CYBER_GRID") {
                        drawLine(
                            color = ImperialGoldPrimary.copy(alpha = 0.12f),
                            start = Offset(0f, cy),
                            end = Offset(size.width, cy),
                            strokeWidth = 1f
                        )
                    }
                    drawCircle(
                        color = MewtwoAuraGlow.copy(alpha = if (isObsidianMode) 0.05f else 0.02f),
                        radius = step * 0.07f,
                        center = Offset(cx, cy)
                    )
                }
            }
        }
        content()
    }
}

/**
 * 2. Tarjeta Bimetálica con Biselado 3D Perimetral (Oro + Plata Cromada + Negro Obsidiana + Morado Mewtwo + Brillos Blancos)
 */
@Composable
fun BimetallicCeramicCard(
    modifier: Modifier = Modifier,
    isObsidianMode: Boolean = true,
    accentMewtwo: Boolean = false,
    outerGold: Boolean = true,
    cornerRadius: Dp = 14.dp,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val outerBrush = when {
        accentMewtwo -> Brush.linearGradient(
            listOf(
                ImperialGoldPrimary,
                HighGlossGoldHighlight,
                MewtwoPsychicPurple,
                SpecularWhite,
                PolishedChrome,
                ImperialGoldPrimary
            )
        )
        outerGold -> GoldMetallicBrush
        else -> ChromeSilverMetallicBrush
    }
    val innerBrush = if (outerGold) ChromeSilverMetallicBrush else GoldMetallicBrush

    val cardBgBrush = if (isObsidianMode) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1E1830),
                Color(0xFF130F1E),
                Color(0xFF0D0A14)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(PureCeramicWhite, PearlIvoryCanvas, Color(0xFFF2F5F8))
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = MewtwoPsychicPurple.copy(alpha = 0.22f),
                spotColor = if (accentMewtwo) MewtwoPsychicPurple.copy(alpha = 0.40f) else ImperialGoldPrimary.copy(alpha = 0.30f)
            )
            .background(outerBrush, RoundedCornerShape(cornerRadius))
            .padding(1.5.dp)
            .background(innerBrush, RoundedCornerShape(cornerRadius - 1.dp))
            .padding(1.2.dp)
            .background(cardBgBrush, RoundedCornerShape(cornerRadius - 2.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * 3. Icono del Sistema con Volumen Esqueumórfico 3D (Oro, Plata, Negro, Blanco y Morado)
 */
@Composable
fun Bimetallic3DIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    goldOuterRing: Boolean = true,
    activeMewtwoGlow: Boolean = false,
    isObsidianMode: Boolean = true
) {
    val outerRingBrush = if (goldOuterRing) GoldMetallicBrush else ChromeSilverMetallicBrush
    val innerRingBrush = if (goldOuterRing) ChromeSilverMetallicBrush else GoldMetallicBrush
    val coreSurfaceBrush = if (isObsidianMode) {
        Brush.radialGradient(
            colors = listOf(
                if (activeMewtwoGlow) MewtwoRoyalIndigo else Color(0xFF28213D),
                MewtwoObsidianBlack
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(SpecularWhite, PearlIvoryCanvas, LiquidSilverLight.copy(alpha = 0.55f))
        )
    }
    val iconTint = when {
        activeMewtwoGlow -> MewtwoAuraGlow
        isObsidianMode && goldOuterRing -> HighGlossGoldHighlight
        isObsidianMode -> SpecularWhite
        goldOuterRing -> DeepGoldShadow
        else -> GraphiteCharcoalText
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 5.dp,
                shape = CircleShape,
                spotColor = if (activeMewtwoGlow) MewtwoPsychicPurple else ImperialGoldPrimary
            )
            .background(outerRingBrush, CircleShape)
            .padding(2.dp)
            .background(innerRingBrush, CircleShape)
            .padding(1.8.dp)
            .background(coreSurfaceBrush, CircleShape)
            .border(
                width = 0.8.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        SpecularWhite.copy(alpha = 0.85f),
                        if (activeMewtwoGlow) MewtwoPsychicPurple else ImperialGoldPrimary.copy(alpha = 0.4f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                brush = Brush.verticalGradient(
                    listOf(SpecularWhite.copy(alpha = 0.70f), Color.Transparent)
                ),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}

/**
 * 4. Botón Compacto Tipo Joya Bimetálica (Oro, Plata, Negro, Blanco y Morado Mewtwo)
 */
@Composable
fun JewelActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimaryGold: Boolean = true,
    isMewtwoAccent: Boolean = false,
    isObsidianMode: Boolean = true,
    enabled: Boolean = true,
    testTag: String = "jewel_button"
) {
    val borderBrush = when {
        isMewtwoAccent -> Brush.linearGradient(
            listOf(HighGlossGoldHighlight, MewtwoPsychicPurple, SpecularWhite, ImperialGoldPrimary)
        )
        isPrimaryGold -> GoldMetallicBrush
        else -> ChromeSilverMetallicBrush
    }

    val bodyBrush = when {
        !enabled -> Brush.verticalGradient(listOf(Color(0xFF2A2733), MewtwoObsidianBlack))
        isMewtwoAccent -> Brush.verticalGradient(
            listOf(MewtwoPsychicPurple, MewtwoDeepViolet, MewtwoObsidianBlack)
        )
        isObsidianMode -> Brush.verticalGradient(
            listOf(Color(0xFF28213D), MewtwoObsidianBlack)
        )
        else -> Brush.verticalGradient(
            listOf(SpecularWhite, PearlIvoryCanvas, LiquidSilverLight.copy(alpha = 0.45f))
        )
    }

    val textColor = when {
        isMewtwoAccent -> SpecularWhite
        isObsidianMode -> if (isPrimaryGold) HighGlossGoldHighlight else SpecularWhite
        else -> GraphiteCharcoalText
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(borderBrush)
            .padding(1.5.dp)
            .background(ChromeSilverMetallicBrush, RoundedCornerShape(18.5.dp))
            .padding(1.dp)
            .background(bodyBrush, RoundedCornerShape(17.5.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isMewtwoAccent) HighGlossGoldHighlight else if (isPrimaryGold) ImperialGoldPrimary else LiquidSilverLight,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 5. Interruptor Físico Metálico Bimetálico
 */
@Composable
fun BimetallicPhysicalSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "bimetallic_switch"
) {
    val trackBrush = if (checked) {
        Brush.horizontalGradient(listOf(MewtwoDeepViolet, MewtwoPsychicPurple, ImperialGoldPrimary))
    } else {
        ChromeSilverMetallicBrush
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .width(48.dp)
            .height(26.dp)
            .shadow(3.dp, RoundedCornerShape(13.dp))
            .clip(RoundedCornerShape(13.dp))
            .background(GoldMetallicBrush)
            .padding(1.5.dp)
            .background(trackBrush, RoundedCornerShape(11.5.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp)
            .testTag(testTag),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .shadow(2.dp, CircleShape)
                .background(
                    if (checked) GoldMetallicBrush else ChromeSilverMetallicBrush,
                    CircleShape
                )
                .padding(2.dp)
                .background(PureCeramicWhite, CircleShape)
        )
    }
}

/**
 * 6. Divisor Metálico Bimetálico (Oro + Plata + Blanco + Pulso Morado Mewtwo)
 */
@Composable
fun BimetallicDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(ImperialGoldPrimary, CircleShape)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.5.dp)
                .background(DividerBimetallicBrush)
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(MewtwoPsychicPurple, CircleShape)
                .border(1.dp, SpecularWhite, CircleShape)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.5.dp)
                .background(DividerBimetallicBrush)
        )
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(ImperialGoldPrimary, CircleShape)
        )
    }
}

/**
 * 7. Encabezado con Degradado Metálico Oro / Plata / Blanco / Morado Mewtwo
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun MetallicGradientTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 17,
    useGoldDominant: Boolean = true
) {
    val brush = if (useGoldDominant) {
        Brush.linearGradient(
            colors = listOf(
                HighGlossGoldHighlight,
                ImperialGoldPrimary,
                SpecularWhite,
                MewtwoAuraGlow,
                HighGlossGoldHighlight
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                SpecularWhite,
                LiquidSilverLight,
                ImperialGoldPrimary,
                SpecularWhite
            )
        )
    }
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            brush = brush,
            letterSpacing = 0.8.sp
        )
    )
}

/**
 * 8. Dial Circular de Alta Relojería Suiza con el Logo Recortado en PNG de KDF-Tools
 */
@Composable
fun SwissChronographTelemetryHeader(
    telemetryPhase: String,
    ramUsedPercent: Int,
    ramSummary: String,
    storageFreeGb: String,
    batteryTempC: Float,
    activeCategoryName: String,
    isObsidianMode: Boolean,
    onLogoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "swiss_dial")
    val secondHandRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chrono_sweep"
    )

    BimetallicCeramicCard(
        modifier = modifier.fillMaxWidth(),
        isObsidianMode = isObsidianMode,
        accentMewtwo = true,
        outerGold = true,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dial Circular Suizo con Marco Estriado (Fluted Bezel) y Logo KDF-Tools en PNG recortado
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clickable(onClick = onLogoClick)
                    .testTag("kdf_logo_dial_button"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Halo psíquico morado Mewtwo detrás del emblema recortado
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MewtwoPsychicPurple.copy(alpha = 0.45f),
                                ImperialGoldPrimary.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Aro exterior estriado de alta relojería (72 muescas en Oro, Plata y Blanco)
                    for (i in 0 until 72) {
                        val angleRad = Math.toRadians((i * 5).toDouble())
                        val innerR = radius * 0.88f
                        val outerR = radius * 0.99f
                        val start = Offset(
                            center.x + (innerR * cos(angleRad)).toFloat(),
                            center.y + (innerR * sin(angleRad)).toFloat()
                        )
                        val end = Offset(
                            center.x + (outerR * cos(angleRad)).toFloat(),
                            center.y + (outerR * sin(angleRad)).toFloat()
                        )
                        drawLine(
                            color = when {
                                i % 6 == 0 -> SpecularWhite
                                i % 2 == 0 -> HighGlossGoldHighlight
                                else -> PolishedChrome
                            },
                            start = start,
                            end = end,
                            strokeWidth = if (i % 6 == 0) 2.5f else 1.3f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Arco de carga RAM en Morado Mewtwo, Oro y Blanco
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                ImperialGoldPrimary,
                                MewtwoPsychicPurple,
                                SpecularWhite,
                                HighGlossGoldHighlight,
                                ImperialGoldPrimary
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = (ramUsedPercent.coerceIn(5, 100) / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Aguja segundera cronógrafo suizo
                    val sweepRad = Math.toRadians((secondHandRotation - 90f).toDouble())
                    val tip = Offset(
                        center.x + (radius * 0.86f * cos(sweepRad)).toFloat(),
                        center.y + (radius * 0.86f * sin(sweepRad)).toFloat()
                    )
                    drawCircle(
                        color = SpecularWhite,
                        radius = 3.5.dp.toPx(),
                        center = tip
                    )
                }

                // Logo KDF-Tools recortado con canal alfa PNG transparente
                Image(
                    painter = painterResource(id = R.drawable.img_kdf_logo),
                    contentDescription = "Logo KDF-Tools PNG Recortado (Toca para ver en alta resolución)",
                    modifier = Modifier.size(74.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Panel derecho de Telemetría y Estado KDF-LuX
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetallicGradientTitle(
                        text = "KDF-TOOLS • LUX",
                        fontSize = 15,
                        useGoldDominant = true
                    )
                    TelemetryStatusBadge(phase = telemetryPhase)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Módulo: $activeCategoryName",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText
                    )
                    Text(
                        text = "Logo PNG (Ver)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MewtwoAuraGlow,
                        modifier = Modifier.clickable(onClick = onLogoClick)
                    )
                }

                // Sub-diales de alta relojería (RAM, Flash Storage, Temp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MiniChronoSubDial(
                        label = "RAM",
                        value = "$ramUsedPercent%",
                        detail = ramSummary,
                        isObsidianMode = isObsidianMode,
                        modifier = Modifier.weight(1f)
                    )
                    MiniChronoSubDial(
                        label = "STORAGE",
                        value = storageFreeGb,
                        detail = "Libre",
                        isObsidianMode = isObsidianMode,
                        modifier = Modifier.weight(1f)
                    )
                    MiniChronoSubDial(
                        label = "TEMP",
                        value = "${batteryTempC}°C",
                        detail = "Núcleo",
                        isObsidianMode = isObsidianMode,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TelemetryStatusBadge(phase: String) {
    val badgeBrush = Brush.horizontalGradient(
        listOf(MewtwoDeepViolet, MewtwoPsychicPurple, ImperialGoldPrimary)
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(badgeBrush)
            .border(1.dp, SpecularWhite.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("telemetry_phase_badge")
    ) {
        Text(
            text = phase,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            ),
            color = SpecularWhite
        )
    }
}

@Composable
private fun MiniChronoSubDial(
    label: String,
    value: String,
    detail: String,
    isObsidianMode: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isObsidianMode) MewtwoObsidianBlack else PearlIvoryCanvas
            )
            .border(1.dp, ChromeSilverMetallicBrush, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = HighGlossGoldHighlight
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (isObsidianMode) SpecularWhite else GraphiteCharcoalText,
                maxLines = 1
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = if (isObsidianMode) LiquidSilverLight else LeadGreySecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 9. Modal de Inspección del Logo KDF-Tools (PNG Recortado con Transparencia Alfa) y Paleta Mewtwo
 */
@Composable
fun KdfLogoShowcaseModal(
    exportStatus: String,
    onExportPngCopy: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        BimetallicCeramicCard(
            isObsidianMode = true,
            accentMewtwo = true,
            outerGold = true,
            contentPadding = PaddingValues(16.dp)
        ) {
            MetallicGradientTitle(
                text = "LOGO OFICIAL KDF-TOOLS (PNG RECORTADO)",
                fontSize = 14
            )
            Text(
                text = "Formato PNG RGBA 32-bit recortado al escudo y corona, integrado como icono adaptativo de la app y emblema central.",
                style = MaterialTheme.typography.bodyMedium,
                color = LiquidSilverLight
            )
            BimetallicDivider()

            // Vista previa del logo recortado sobre cuadrícula oscura/púrpura para apreciar el recorte PNG
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MewtwoPsychicPurple.copy(alpha = 0.35f),
                                MewtwoRoyalIndigo,
                                MewtwoObsidianBlack
                            )
                        )
                    )
                    .border(1.5.dp, GoldMetallicBrush, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_kdf_logo),
                    contentDescription = "Logo KDF-Tools PNG Recortado",
                    modifier = Modifier.size(185.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Paleta Oficial Tema Oscuro Mewtwo (5 Colores Principales):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = HighGlossGoldHighlight
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorSwatchPill("ORO", "#D4AF37", ImperialGoldPrimary, MewtwoObsidianBlack, Modifier.weight(1f))
                ColorSwatchPill("PLATA", "#E2E8F0", LiquidSilverLight, MewtwoObsidianBlack, Modifier.weight(1f))
                ColorSwatchPill("NEGRO", "#0B0912", MewtwoObsidianBlack, SpecularWhite, Modifier.weight(1f))
                ColorSwatchPill("BLANCO", "#FFFFFF", PureCeramicWhite, MewtwoObsidianBlack, Modifier.weight(1f))
                ColorSwatchPill("MORADO", "#9B4DFF", MewtwoPsychicPurple, SpecularWhite, Modifier.weight(1f))
            }

            if (exportStatus.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = exportStatus,
                    style = MaterialTheme.typography.labelSmall,
                    color = HighGlossGoldHighlight
                )
            }

            BimetallicDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JewelActionButton(
                    text = "Exportar Copia PNG",
                    isPrimaryGold = true,
                    isMewtwoAccent = true,
                    isObsidianMode = true,
                    onClick = onExportPngCopy,
                    testTag = "btn_export_logo_png"
                )
                JewelActionButton(
                    text = "Cerrar",
                    isPrimaryGold = false,
                    isObsidianMode = true,
                    onClick = onDismiss,
                    testTag = "btn_close_logo_modal"
                )
            }
        }
    }
}

@Composable
private fun ColorSwatchPill(
    name: String,
    hex: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, GoldMetallicBrush, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            text = hex,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
            color = textColor.copy(alpha = 0.85f)
        )
    }
}
