package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// === PALETA OFICIAL KDF-TOOLS: ORO, PLATA, NEGRO, BLANCO Y MORADO MEWTWO ===

// 1. NEGRO (Fondos Principales del Tema Oscuro Mewtwo Bimetálico)
val MewtwoObsidianBlack = Color(0xFF0B0912)
val MewtwoDarkPanel = Color(0xFF141122)
val MewtwoElevatedSurface = Color(0xFF1E1930)
val GraphiteCharcoalText = Color(0xFF16151A)

// 2. BLANCO (Brillos Especulares, Núcleos Luminosos y Tipografía Principal en Tema Oscuro)
val PureCeramicWhite = Color(0xFFFFFFFF)
val SpecularWhite = Color(0xFFFFFFFF)
val PearlIvoryCanvas = Color(0xFFF8F9FA)
val DamaskFiligreeLight = Color(0xFFE8ECEF)
val PorcelainCardSurface = Color(0xFFFCFCFD)

// 3. ORO (Metal Real Principal - Corona y Letras KDF)
val ImperialGoldPrimary = Color(0xFFD4AF37)
val HighGlossGoldHighlight = Color(0xFFFFDF73)
val DeepGoldShadow = Color(0xFF997A15)
val AntiqueGoldBorder = Color(0xFFB8860B)

// 4. PLATA (Metal Primario - Escudo Cepillado, Engranajes y Llaves)
val LiquidSilverLight = Color(0xFFE2E8F0)
val PolishedChrome = Color(0xFFBCCCDC)
val BrushedSteelShadow = Color(0xFF829AB1)
val DeepChromeGunmetal = Color(0xFF334E68)
val LeadGreySecondaryText = Color(0xFF9FB3C8)

// 5. MORADO (Energía Psíquica Mewtwo & Gema de la Corona)
val MewtwoPsychicPurple = Color(0xFF9B4DFF)
val MewtwoAuraGlow = Color(0xFFC77DFF)
val MewtwoDeepViolet = Color(0xFF4A148C)
val MewtwoRoyalIndigo = Color(0xFF2D0C57)

// Estados de Telemetría
val TelemetrySafeEmerald = Color(0xFF10B981)
val TelemetryWarningAmber = Color(0xFFF59E0B)
val TelemetryCriticalCrimson = Color(0xFFEF4444)

// Degradados Bimetálicos + Energía Mewtwo
val GoldMetallicBrush = Brush.linearGradient(
    colors = listOf(
        DeepGoldShadow,
        ImperialGoldPrimary,
        HighGlossGoldHighlight,
        SpecularWhite,
        ImperialGoldPrimary,
        DeepGoldShadow
    )
)

val ChromeSilverMetallicBrush = Brush.linearGradient(
    colors = listOf(
        BrushedSteelShadow,
        PolishedChrome,
        SpecularWhite,
        LiquidSilverLight,
        BrushedSteelShadow
    )
)

val BimetallicGoldSilverBrush = Brush.linearGradient(
    colors = listOf(
        ImperialGoldPrimary,
        HighGlossGoldHighlight,
        SpecularWhite,
        LiquidSilverLight,
        PolishedChrome,
        HighGlossGoldHighlight,
        DeepGoldShadow
    )
)

val MewtwoBimetallicBrush = Brush.linearGradient(
    colors = listOf(
        ImperialGoldPrimary,
        HighGlossGoldHighlight,
        MewtwoAuraGlow,
        MewtwoPsychicPurple,
        PolishedChrome,
        ImperialGoldPrimary
    )
)

val DividerBimetallicBrush = Brush.horizontalGradient(
    colors = listOf(
        ImperialGoldPrimary,
        HighGlossGoldHighlight,
        PolishedChrome,
        SpecularWhite,
        MewtwoPsychicPurple,
        SpecularWhite,
        PolishedChrome,
        HighGlossGoldHighlight,
        ImperialGoldPrimary
    )
)
