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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import com.example.ui.theme.LeadGreySecondaryText
import com.example.ui.theme.LiquidSilverLight
import com.example.ui.theme.MewtwoAuraGlow
import com.example.ui.theme.MewtwoObsidianBlack
import com.example.ui.theme.MewtwoPsychicPurple
import com.example.ui.theme.PearlIvoryCanvas
import com.example.ui.theme.PolishedChrome
import com.example.ui.theme.SpecularWhite
import com.example.ui.theme.TelemetrySafeEmerald
import java.io.File

/**
 * Pantalla Dedicada a Personalizar Fondo de la Aplicación
 * Implementa la lógica completa:
 * - Selección de fotos de galería con Android Photo Picker (zero permissions).
 * - Selección de Presets Bimetálicos (Filigrana Mewtwo, Emblema KDF, Cyber Grid, Nebulosa Púrpura, Escudo Real).
 * - Selección de Renders generados previamente en el Estudio IA.
 * - Control de atenuación / velo oscuro (Dim Alpha) para legibilidad.
 * - Control de desenfoque / blur.
 * - Control de realce de contraste adaptativo.
 * - Restablecimiento a valores de fábrica.
 */
@Composable
fun WallpaperCustomizerScreen(
    uiState: KdfUiState,
    generatedImages: List<GeneratedImageEntity>,
    onSelectPreset: (String) -> Unit,
    onPickGalleryUri: (Uri) -> Unit,
    onSelectGeneratedImage: (String) -> Unit,
    onUpdateDimAlpha: (Float) -> Unit,
    onUpdateBlurRadius: (Float) -> Unit,
    onUpdateContrastBoost: (Float) -> Unit,
    onResetToDefault: () -> Unit
) {
    val isDark = uiState.isObsidianMode

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickGalleryUri(uri)
        }
    }

    val previewBitmap = remember(uiState.customBackgroundPath) {
        if (!uiState.customBackgroundPath.isNullOrBlank()) {
            val f = File(uiState.customBackgroundPath)
            if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
        } else {
            null
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // Tarjeta Principal de Personalización de Fondo
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
                        icon = Icons.Filled.Wallpaper,
                        contentDescription = "Personalizar Fondo",
                        size = 38.dp,
                        goldOuterRing = true,
                        activeMewtwoGlow = true,
                        isObsidianMode = isDark
                    )
                    Column {
                        MetallicGradientTitle(
                            text = "PERSONALIZAR FONDO DE LA APLICACIÓN",
                            fontSize = 14
                        )
                        Text(
                            text = "Lógica completa de diseño: Galería, Presets Bimetálicos, Atenuación y Contraste",
                            style = MaterialTheme.typography.labelSmall,
                            color = MewtwoAuraGlow
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
                    testTag = "btn_pick_gallery_wallpaper_screen"
                )
            }

            BimetallicDivider()

            // Previsualización en Vivo del Fondo Seleccionado
            Text(
                text = "Previsualización del Fondo Activo:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = HighGlossGoldHighlight
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, GoldMetallicBrush, RoundedCornerShape(12.dp))
            ) {
                if (previewBitmap != null && uiState.backgroundPreset == "CUSTOM_IMAGE") {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = "Previsualización Fondo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (uiState.backgroundPreset == "KDF_EMBLEM_WALLPAPER") {
                    Image(
                        painter = painterResource(id = R.drawable.img_kdf_logo),
                        contentDescription = "Previsualización Emblema",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                when (uiState.backgroundPreset) {
                                    "PURPLE_NEBULA" -> Brush.verticalGradient(listOf(Color(0xFF2D1654), Color(0xFF0C0517)))
                                    "GOLD_CYBER_GRID" -> Brush.verticalGradient(listOf(Color(0xFF281F0D), Color(0xFF0F0B05)))
                                    "ROYAL_SHIELD" -> Brush.verticalGradient(listOf(Color(0xFF1B1E34), Color(0xFF0B0D18)))
                                    else -> Brush.verticalGradient(listOf(MewtwoObsidianBlack, Color(0xFF140F23)))
                                }
                            )
                    )
                }
                // Capa de atenuación
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MewtwoObsidianBlack.copy(alpha = uiState.backgroundDimAlpha))
                )
                // Insignia sobre la previsualización
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Preset Activo: ${uiState.backgroundPreset}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = HighGlossGoldHighlight
                    )
                    Text(
                        text = "Velo Oscuro: ${(uiState.backgroundDimAlpha * 100).toInt()}% • Contraste: ${(uiState.backgroundContrastBoost * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SpecularWhite
                    )
                }
            }

            BimetallicDivider()

            // 1. Selector de Presets Bimetálicos
            Text(
                text = "1. Estilos y Presets Bimetálicos Mewtwo:",
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
                    "MEWTWO_FILIGREE" to "Filigrana Mewtwo (Oficial)",
                    "KDF_EMBLEM_WALLPAPER" to "Emblema Real KDF Completo",
                    "GOLD_CYBER_GRID" to "Cyber Grid Oro / Plata",
                    "PURPLE_NEBULA" to "Nebulosa Violeta Psíquica",
                    "ROYAL_SHIELD" to "Escudo Real Bimetálico",
                    "CUSTOM_IMAGE" to "Imagen de Galería Activa"
                ).forEach { (presetKey, label) ->
                    MetallicChipPill(
                        label = label,
                        selected = uiState.backgroundPreset == presetKey,
                        isObsidianMode = isDark,
                        onClick = { onSelectPreset(presetKey) }
                    )
                }
            }

            // 2. Renders del Estudio IA
            if (generatedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "2. Usar render del Estudio IA como fondo:",
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
                    generatedImages.forEachIndexed { idx, img ->
                        val isSel = uiState.customBackgroundPath == img.filePath && uiState.backgroundPreset == "CUSTOM_IMAGE"
                        MetallicChipPill(
                            label = "Render #${idx + 1} (${img.imageSize})",
                            selected = isSel,
                            isObsidianMode = isDark,
                            onClick = { onSelectGeneratedImage(img.filePath) }
                        )
                    }
                }
            }

            BimetallicDivider()

            // 3. Controles Deslizantes de Ajuste Fino
            Text(
                text = "3. Lógica de Atenuación y Contraste:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = HighGlossGoldHighlight
            )

            // Velo Oscuro / Dim Alpha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Atenuación / Velo Oscuro (Dimming):",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                )
                Text(
                    text = "${(uiState.backgroundDimAlpha * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = HighGlossGoldHighlight
                )
            }
            Slider(
                value = uiState.backgroundDimAlpha,
                onValueChange = onUpdateDimAlpha,
                valueRange = 0.10f..0.95f,
                colors = SliderDefaults.colors(
                    thumbColor = HighGlossGoldHighlight,
                    activeTrackColor = MewtwoPsychicPurple,
                    inactiveTrackColor = PolishedChrome
                ),
                modifier = Modifier.testTag("slider_wallpaper_dim_screen")
            )

            // Realce de Contraste Adaptativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Realce de Contraste GPU:",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) LiquidSilverLight else LeadGreySecondaryText
                )
                Text(
                    text = "${(uiState.backgroundContrastBoost * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = HighGlossGoldHighlight
                )
            }
            Slider(
                value = uiState.backgroundContrastBoost,
                onValueChange = onUpdateContrastBoost,
                valueRange = 0.80f..1.60f,
                colors = SliderDefaults.colors(
                    thumbColor = ImperialGoldPrimary,
                    activeTrackColor = ImperialGoldPrimary,
                    inactiveTrackColor = PolishedChrome
                ),
                modifier = Modifier.testTag("slider_wallpaper_contrast_screen")
            )

            BimetallicDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Todos los cambios se guardan automáticamente.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TelemetrySafeEmerald
                )
                JewelActionButton(
                    text = "Restablecer Fondo",
                    icon = Icons.Filled.Refresh,
                    isPrimaryGold = false,
                    isMewtwoAccent = false,
                    isObsidianMode = isDark,
                    onClick = onResetToDefault,
                    testTag = "btn_reset_wallpaper_default"
                )
            }
        }
    }
}
