package com.tecnoayuda.extractorfacturas

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Log

@Serializable
data class InvoiceData(
    val proveedor: String? = null,
    val fecha: String? = null,
    val total: String? = null,
    val link_facturacion: String? = null
)

class GeminiInvoiceParser {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun parseInvoice(rawText: String): InvoiceData? = withContext(Dispatchers.IO) {
        try {
            val prompt = "Eres un sistema experto analizando texto de tickets y facturas extraído por OCR. Tu tarea es encontrar 4 datos clave, incluso si hay ruido o errores de lectura. 1. proveedor (el nombre del negocio o emisor). 2. fecha (conviértela a formato YYYY-MM-DD). 3. total (el precio final cobrado, busca palabras como Total, Importe, Monto o signos de $). 4. link_facturacion (busca URLs, dominios web, www o palabras como portal/factura electronica). Devuelve ÚNICAMENTE un JSON válido con las claves: \"proveedor\", \"fecha\", \"total\", \"link_facturacion\". Si es absolutamente imposible encontrar un dato, su valor en el JSON debe ser null. Texto: $rawText"

            val response = generativeModel.generateContent(prompt)
            val jsonString = response.text
            
            if (jsonString != null) {
                // Ensure it's clean JSON by parsing with kotlinx.serialization
                return@withContext json.decodeFromString<InvoiceData>(jsonString)
            }
        } catch (e: Exception) {
            Log.e("GeminiInvoiceParser", "Error parsing invoice", e)
        }
        return@withContext null
    }
}