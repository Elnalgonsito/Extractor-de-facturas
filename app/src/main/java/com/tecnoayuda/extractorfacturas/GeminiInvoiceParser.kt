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
    val sucursal: String? = null,
    val fecha_hora: String? = null,
    val regimen_fiscal: String? = null,
    val metodo_pago: String? = null,
    val articulos: List<InvoiceItem>? = null,
    val subtotal: Double? = null,
    val impuestos: String? = null,
    val total: Double? = null,
    val link_facturacion: String? = null
)

@Serializable
data class InvoiceItem(
    val cantidad: Double? = null,
    val descripcion: String? = null,
    val importe: Double? = null
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
            val prompt = "Eres un analista de datos contables. Analiza este texto de OCR de un ticket. Ignora por completo encuestas, beneficios, puntos, anuncios y números de atención al cliente. Extrae solo la información de la compra y devuelve ÚNICAMENTE este formato JSON: {\"proveedor\": \"nombre\", \"sucursal\": \"lugar\", \"fecha_hora\": \"YYYY-MM-DD HH:MM\", \"regimen_fiscal\": \"código y descripción\", \"metodo_pago\": \"método e información\", \"articulos\": [{\"cantidad\": 0.0, \"descripcion\": \"nombre\", \"importe\": 0.0}], \"subtotal\": 0.0, \"impuestos\": \"detalle de tasas\", \"total\": 0.0, \"link_facturacion\": \"url\"}. Si falta algo, usa null. Texto: $rawText"

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