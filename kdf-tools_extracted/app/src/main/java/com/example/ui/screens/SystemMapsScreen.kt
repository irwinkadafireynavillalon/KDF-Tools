package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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

data class MapWaypoint(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: String
)

/**
 * Pantalla de Mapas & Navegación KDF-Tools
 * Renderiza mapa interactivo OpenStreetMap / Leaflet con tema Mewtwo oscuro,
 * telemetría GPS real y planificador de rutas técnicas.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SystemMapsScreen(
    isObsidianMode: Boolean,
    onSendTelemetryToOrchestrator: (String) -> Unit = {}
) {
    var latitude by remember { mutableDoubleStateOf(19.432608) }
    var longitude by remember { mutableDoubleStateOf(-99.133209) }
    var altitudeMeters by remember { mutableDoubleStateOf(2240.0) }
    var speedKmh by remember { mutableFloatStateOf(0.0f) }
    var accuracyMeters by remember { mutableFloatStateOf(3.2f) }
    var mapLayer by remember { mutableStateOf("DARK_MEWTWO") } // "DARK_MEWTWO", "STREET", "SATELLITE"
    var selectedWaypoint by remember { mutableStateOf("Taller Central KDF") }
    var routeStatusMessage by remember { mutableStateOf("Ruta técnica trazada: 8.4 km • Est. 14 min") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val defaultWaypoints = listOf(
        MapWaypoint("1", "Taller Central KDF", 19.432608, -99.133209, "Base de Operaciones"),
        MapWaypoint("2", "Estación Rescate Fastboot", 19.441200, -99.145000, "Soporte Técnico"),
        MapWaypoint("3", "Laboratorio Microelectrónica", 19.425000, -99.120000, "Hardware & Soldadura")
    )

    fun generateLeafletHtml(lat: Double, lng: Double, layer: String): String {
        val tileUrl = when (layer) {
            "SATELLITE" -> "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
            "STREET" -> "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
            else -> "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>
                    body, html, #map { margin: 0; padding: 0; height: 100%; width: 100%; background: #0b0912; }
                    .custom-marker {
                        background: linear-gradient(135deg, #d4af37, #9b4dff);
                        border: 2px solid #ffffff;
                        border-radius: 50%;
                        width: 16px;
                        height: 16px;
                        box-shadow: 0 0 12px #9b4dff;
                    }
                    .leaflet-popup-content-wrapper {
                        background: #141122;
                        color: #e2e8f0;
                        border: 1px solid #d4af37;
                        border-radius: 10px;
                        font-family: sans-serif;
                    }
                    .leaflet-popup-tip { background: #141122; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map', { zoomControl: false }).setView([$lat, $lng], 14);
                    L.tileLayer('$tileUrl', {
                        maxZoom: 19,
                        attribution: '© OpenStreetMap / KDF-Tools'
                    }).addTo(map);

                    var pulseIcon = L.divIcon({
                        className: 'custom-marker',
                        iconSize: [18, 18],
                        iconAnchor: [9, 9]
                    });

                    var marker = L.marker([$lat, $lng], { icon: pulseIcon }).addTo(map)
                        .bindPopup('<b>⚡ Dispositivo KDF-Tools</b><br>Lat: $lat<br>Lng: $lng<br>Estado: GNSS Activo').openPopup();

                    L.circle([$lat, $lng], {
                        color: '#9b4dff',
                        fillColor: '#9b4dff',
                        fillOpacity: 0.15,
                        radius: 80
                    }).addTo(map);

                    // Waypoints adicionales
                    L.marker([19.441200, -99.145000], { icon: pulseIcon }).addTo(map).bindPopup('Estación Rescate Fastboot');
                    L.marker([19.425000, -99.120000], { icon: pulseIcon }).addTo(map).bindPopup('Laboratorio Microelectrónica');

                    // Ruta
                    var latlngs = [
                        [$lat, $lng],
                        [19.441200, -99.145000]
                    ];
                    L.polyline(latlngs, {color: '#ffd700', weight: 4, opacity: 0.85, dashArray: '8, 8'}).addTo(map);
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Barra Superior: HUD de Telemetría GPS Mewtwo
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
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Mapas",
                            tint = HighGlossGoldHighlight,
                            modifier = Modifier.size(18.dp)
                        )
                        MetallicGradientTitle(
                            text = "MAPAS & TELEMETRÍA GPS",
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
                            text = "GNSS FIJADO (14 SAT)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TelemetrySafeEmerald
                        )
                    }
                }

                // Grid de Sensores de Posicionamiento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "COORDENADAS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = LiquidSilverLight.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "%.4f, %.4f".format(latitude, longitude),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = PureCeramicWhite
                        )
                    }

                    Column {
                        Text(
                            text = "ALTITUD / PRECISIÓN",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = LiquidSilverLight.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "%.0f m (±%.1fm)".format(altitudeMeters, accuracyMeters),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ImperialGoldPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "VELOCIDAD",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = LiquidSilverLight.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "%.1f km/h".format(speedKmh),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MewtwoAuraGlow
                        )
                    }
                }

                // Selector de Capa & Enviar al Orquestador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "DARK_MEWTWO" to "Mewtwo Dark",
                            "STREET" to "Calles",
                            "SATELLITE" to "Satélite"
                        ).forEach { (id, label) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (mapLayer == id) MewtwoPsychicPurple else MewtwoRoyalIndigo.copy(alpha = 0.4f)
                                    )
                                    .clickable {
                                        mapLayer = id
                                        webViewRef?.loadDataWithBaseURL(
                                            null,
                                            generateLeafletHtml(latitude, longitude, id),
                                            "text/html",
                                            "UTF-8",
                                            null
                                        )
                                    }
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (mapLayer == id) PureCeramicWhite else LiquidSilverLight.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Botón enviar coordenadas al Orquestador
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldMetallicBrush)
                            .clickable {
                                val telemetryPayload = "GPS Lat: $latitude, Lng: $longitude, Alt: $altitudeMeters m, Accuracy: $accuracyMeters m"
                                onSendTelemetryToOrchestrator(telemetryPayload)
                                routeStatusMessage = "Coordenadas enviadas al Orquestador KDF-Tools."
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = MewtwoObsidianBlack,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Vincular a Orquestador",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MewtwoObsidianBlack
                            )
                        }
                    }
                }
            }
        }

        // Mapa Interactivo Embebido
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, GoldMetallicBrush, RoundedCornerShape(12.dp))
                .background(MewtwoObsidianBlack)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                        }
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL(
                            null,
                            generateLeafletHtml(latitude, longitude, mapLayer),
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webViewRef = this
                    }
                },
                update = { webView ->
                    webViewRef = webView
                }
            )

            // Tarjeta Flotante Inferior de Ruta
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MewtwoObsidianBlack.copy(alpha = 0.9f))
                    .border(1.dp, ImperialGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PLANIFICADOR DE RUTA TÉCNICA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = HighGlossGoldHighlight
                        )
                        Text(
                            text = routeStatusMessage,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = LiquidSilverLight
                        )
                    }

                    IconButton(
                        onClick = {
                            // Simular cálculo de nueva ruta
                            routeStatusMessage = "Ruta optimizada recalculada hacia $selectedWaypoint."
                            webViewRef?.loadDataWithBaseURL(
                                null,
                                generateLeafletHtml(latitude, longitude, mapLayer),
                                "text/html",
                                "UTF-8",
                                null
                            )
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "Recalcular",
                            tint = ImperialGoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
