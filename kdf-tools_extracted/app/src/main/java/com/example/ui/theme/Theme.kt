package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema Oscuro Principal KDF-Tools (Oro, Plata, Negro, Blanco y Morado Mewtwo)
private val BimetallicObsidianDarkScheme = darkColorScheme(
    primary = ImperialGoldPrimary,
    onPrimary = MewtwoObsidianBlack,
    primaryContainer = MewtwoRoyalIndigo,
    onPrimaryContainer = HighGlossGoldHighlight,
    secondary = MewtwoPsychicPurple,
    onSecondary = PureCeramicWhite,
    secondaryContainer = MewtwoDeepViolet,
    onSecondaryContainer = SpecularWhite,
    tertiary = LiquidSilverLight,
    onTertiary = MewtwoObsidianBlack,
    background = MewtwoObsidianBlack,
    onBackground = PureCeramicWhite,
    surface = MewtwoDarkPanel,
    onSurface = PureCeramicWhite,
    surfaceVariant = MewtwoElevatedSurface,
    onSurfaceVariant = LiquidSilverLight,
    outline = ImperialGoldPrimary,
    outlineVariant = PolishedChrome
)

// Modo Alternativo Porcelana Blanca Bimetálica
private val BimetallicPorcelainLightScheme = lightColorScheme(
    primary = ImperialGoldPrimary,
    onPrimary = GraphiteCharcoalText,
    primaryContainer = LiquidSilverLight,
    onPrimaryContainer = GraphiteCharcoalText,
    secondary = MewtwoPsychicPurple,
    onSecondary = PureCeramicWhite,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = MewtwoDeepViolet,
    tertiary = DeepChromeGunmetal,
    onTertiary = PureCeramicWhite,
    background = PearlIvoryCanvas,
    onBackground = GraphiteCharcoalText,
    surface = PureCeramicWhite,
    onSurface = GraphiteCharcoalText,
    surfaceVariant = Color(0xFFF1F5F8),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = PolishedChrome
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) BimetallicObsidianDarkScheme else BimetallicPorcelainLightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
