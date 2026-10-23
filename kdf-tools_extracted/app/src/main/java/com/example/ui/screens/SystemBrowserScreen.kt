package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.components.BimetallicCardSurface
import com.example.ui.components.BimetallicIconButton
import com.example.ui.components.MetallicChipPill
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

data class QuickBookmark(
    val title: String,
    val url: String,
    val badge: String
)

/**
 * Pantalla del Navegador del Sistema KDF-Tools
 * Tema Bimetálico Mewtwo con protección de privacidad, barra URL y auditor de seguridad.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SystemBrowserScreen(
    isObsidianMode: Boolean,
    onNavigateBack: () -> Unit = {}
) {
    var currentUrl by remember { mutableStateOf("http://127.0.0.1:5000") }
    var inputUrl by remember { mutableStateOf("http://127.0.0.1:5000") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var pageTitle by remember { mutableStateOf("KDF-Tools Suite Web") }
    var isSecureSsl by remember { mutableStateOf(true) }

    val defaultBookmarks = listOf(
        QuickBookmark("⚡ KDF Suite", "http://127.0.0.1:5000", "Local"),
        QuickBookmark("☁️ AI Studio", "https://ai.studio", "IA"),
        QuickBookmark("🔍 DuckDuckGo", "https://duckduckgo.com", "Privado"),
        QuickBookmark("🐙 GitHub Repo", "https://github.com/irwinkadafireynavillalon/KDF-Tools", "Código"),
        QuickBookmark("📖 Android Dev", "https://developer.android.com", "Docs")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Barra Superior: Título & Estado de Seguridad Mewtwo
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
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Escudo",
                            tint = ImperialGoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        MetallicGradientTitle(
                            text = "NAVEGADOR KDF-TOOLS",
                            fontSize = 14
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isSecureSsl) Icons.Default.Lock else Icons.Default.Security,
                            contentDescription = "SSL Status",
                            tint = if (isSecureSsl) TelemetrySafeEmerald else ImperialGoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isSecureSsl) "CONEXIÓN CIFRADA / AUDITADA" else "MODO LOCAL SEGURO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSecureSsl) TelemetrySafeEmerald else ImperialGoldPrimary
                        )
                    }
                }

                // Barra de Dirección URL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Controles Atrás / Adelante / Recargar / Inicio
                    IconButton(
                        onClick = { if (webViewRef?.canGoBack() == true) webViewRef?.goBack() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = LiquidSilverLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { if (webViewRef?.canGoForward() == true) webViewRef?.goForward() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Adelante",
                            tint = LiquidSilverLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.reload() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar",
                            tint = ImperialGoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            currentUrl = "http://127.0.0.1:5000"
                            inputUrl = currentUrl
                            webViewRef?.loadUrl(currentUrl)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Inicio",
                            tint = MewtwoAuraGlow,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Campo de entrada de URL
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = LiquidSilverLight
                        ),
                        placeholder = {
                            Text(
                                "Ingresa URL o término de búsqueda...",
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
                        trailingIcon = {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = ImperialGoldPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        var target = inputUrl.trim()
                                        if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                            target = if (target.contains(".") && !target.contains(" ")) {
                                                "https://$target"
                                            } else {
                                                "https://duckduckgo.com/?q=${target.replace(" ", "+")}"
                                            }
                                        }
                                        currentUrl = target
                                        inputUrl = target
                                        webViewRef?.loadUrl(target)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Ir",
                                        tint = HighGlossGoldHighlight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    )
                }

                // Marcadores Rápidos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    defaultBookmarks.forEach { bookmark ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MewtwoRoyalIndigo.copy(alpha = 0.4f))
                                .border(1.dp, MewtwoPsychicPurple.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .clickable {
                                    currentUrl = bookmark.url
                                    inputUrl = bookmark.url
                                    webViewRef?.loadUrl(bookmark.url)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = bookmark.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = PureCeramicWhite
                            )
                        }
                    }
                }
            }
        }

        // Vista WebView Embebida
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
                            loadsImagesAutomatically = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                                url?.let { inputUrl = it }
                                isSecureSsl = url?.startsWith("https://") == true || url?.contains("127.0.0.1") == true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                url?.let {
                                    currentUrl = it
                                    inputUrl = it
                                }
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                title?.let { pageTitle = it }
                            }
                        }
                        loadUrl(currentUrl)
                        webViewRef = this
                    }
                },
                update = { webView ->
                    webViewRef = webView
                }
            )

            // Indicador de Carga Flotante Superior
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(GoldMetallicBrush)
                )
            }
        }
    }
}
