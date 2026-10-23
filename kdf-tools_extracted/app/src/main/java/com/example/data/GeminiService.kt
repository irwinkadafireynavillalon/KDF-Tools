package com.example.data

import android.content.Context
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val temperature: Float? = null,
    val imageConfig: ImageConfig? = null,
    val responseModalities: List<String>? = null
)

@Serializable
data class ImageConfig(
    val aspectRatio: String,
    val imageSize: String // "1K", "2K", "4K"
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

interface GeminiRestApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

class GeminiService(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiRestApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiRestApi::class.java)
    }

    /**
     * Consola de Asistencia KDF-LuX y Diagnóstico de Bio-Ciencia (usa gemini-3.5-flash)
     */
    suspend fun askKdfAssistant(
        userPrompt: String,
        systemContext: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "[KDF-LuX IA Local]: Para conectar con el motor en la nube Gemini, configura tu GEMINI_API_KEY en el panel Secrets de AI Studio.\n\n" +
                "Diagnóstico Técnico KDF-LuX para tu consulta (\"$userPrompt\"):\n" +
                "• Scripts verificados en KDF-LuX/scripts/ (enter_safe_mode.sh, disable_blocker.sh, clean_memory.sh, unlock_bootloader.sh, edl_flasher.sh).\n" +
                "• Contexto activo: $systemContext"
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            generationConfig = GenerationConfig(temperature = 0.4f),
            systemInstruction = Content(
                parts = listOf(
                    Part(
                        text = "Eres el núcleo de inteligencia KDF-Tools (Arquitectura KDF-LuX). " +
                            "Dominas ingeniería Android (ADB, Fastboot, Qualcomm EDL 9008, GSI, particiones A/B) " +
                            "y también bio-informática / telemetría de bio-sensores. Responde en español de forma precisa, técnica y directa. " +
                            "Contexto actual del dispositivo: $systemContext"
                    )
                )
            )
        )

        try {
            val response = api.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                ?: "Sin respuesta del modelo."
        } catch (e: Exception) {
            "Error de comunicación Gemini API: ${e.message}"
        }
    }

    /**
     * Generación de Imágenes de Alta Calidad usando obligatoriamente el modelo `gemini-3-pro-image-preview`
     * con soporte explícito para resoluciones `1K`, `2K` y `4K`.
     */
    suspend fun generateHighQualityImage(
        prompt: String,
        imageSize: String, // "1K", "2K", or "4K"
        aspectRatio: String, // "1:1", "16:9", "9:16", "4:3"
        rendersDir: File
    ): Result<Pair<File, String>> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Configura tu GEMINI_API_KEY en el panel Secrets de AI Studio para generar imágenes $imageSize con gemini-3-pro-image-preview.")
            )
        }

        val validSize = when (imageSize.uppercase()) {
            "1K", "2K", "4K" -> imageSize.uppercase()
            else -> "1K"
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(
                imageConfig = ImageConfig(
                    aspectRatio = aspectRatio,
                    imageSize = validSize
                ),
                responseModalities = listOf("TEXT", "IMAGE")
            )
        )

        try {
            val response = api.generateContent(
                model = "gemini-3-pro-image-preview",
                apiKey = apiKey,
                request = request
            )
            val parts = response.candidates?.firstOrNull()?.content?.parts ?: emptyList()
            val imagePart = parts.firstOrNull { it.inlineData != null }
            val accompanyingText = parts.firstOrNull { it.text != null }?.text ?: "Renderizado $validSize completado con gemini-3-pro-image-preview."

            if (imagePart?.inlineData != null) {
                val bytes = Base64.decode(imagePart.inlineData.data, Base64.DEFAULT)
                if (!rendersDir.exists()) rendersDir.mkdirs()
                val outFile = File(rendersDir, "kdf_render_${validSize}_${System.currentTimeMillis()}.png")
                outFile.writeBytes(bytes)
                Result.success(Pair(outFile, accompanyingText))
            } else {
                Result.failure(IllegalStateException("La respuesta de gemini-3-pro-image-preview no incluyó datos de imagen inline. Detalle: $accompanyingText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
