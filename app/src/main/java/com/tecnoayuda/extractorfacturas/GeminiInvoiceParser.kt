package com.tecnoayuda.extractorfacturas

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

@Serializable
data class InvoiceData(
    val proveedor: String? = null,
    val sucursal: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val regimen_fiscal: String? = null,
    val metodo_pago: String? = null,
    val articulos: List<InvoiceItem>? = null,
    val subtotal: Double? = null,
    val iva: Double? = null,
    val ieps: Double? = null,
    val impuestos: List<InvoiceTax>? = null,
    val total: Double? = null,
    val link_facturacion: String? = null
)

@Serializable
data class InvoiceTax(
    val tipo: String? = null,
    val monto: Double? = null
)

@Serializable
data class InvoiceItem(
    val cantidad: Double? = null,
    val descripcion: String? = null,
    val importe: Double? = null
)

class GeminiInvoiceParser {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun parseInvoice(bitmap: Bitmap): InvoiceData = withContext(Dispatchers.IO) {
        val promptText = """
            Eres una API estricta de extracción de datos contables de tickets y facturas.
            Tu única tarea es extraer la información solicitada de la imagen y devolverla EXCLUSIVAMENTE en formato JSON.
            No debes incluir saludos, explicaciones, ni etiquetas de código (como ```json o ```).
            
            Estructura JSON requerida:
            {
                "proveedor": "Nombre del emisor",
                "sucursal": "Nombre o dirección de la sucursal",
                "fecha": "Fecha de emisión en formato YYYY-MM-DD",
                "hora": "Hora de emisión en formato HH:MM:SS",
                "regimen_fiscal": "Régimen fiscal (si aplica)",
                "metodo_pago": "Método de pago (efectivo, tarjeta, etc.)",
                "articulos": [
                    {
                        "cantidad": 1.0,
                        "descripcion": "Descripción del producto o servicio",
                        "importe": 100.0
                    }
                ],
                "subtotal": 100.0,
                "iva": 16.0,
                "ieps": 0.0,
                "impuestos": [
                    {
                        "tipo": "IVA",
                        "monto": 16.0
                    }
                ],
                "total": 116.0,
                "link_facturacion": "URL para facturar (si existe)"
            }
            
            Si un dato no existe, déjalo como null (o [] para listas).
            Asegúrate de que los valores numéricos sean de tipo número y no cadenas.
        """.trimIndent()

        // Scale and compress
        val maxDim = 1024
        val scale = Math.min(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
        val scaled = if (scale < 1) Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt(),
            (bitmap.height * scale).toInt(),
            true
        ) else bitmap
        val stream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val base64Image = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)

        // Build JSON payload
        val payload = JSONObject()
        payload.put("model", "llama-3.2-11b-vision-instruct")
        // payload.put("model", "llama-3.2-90b-vision-preview") // You could also try the 90b model if 11b acts up

        val message = JSONObject()
        message.put("role", "user")

        val contentArray = JSONArray()

        val textContent = JSONObject()
        textContent.put("type", "text")
        textContent.put("text", promptText)
        contentArray.put(textContent)

        val imageContent = JSONObject()
        imageContent.put("type", "image_url")
        val imageUrl = JSONObject()
        imageUrl.put("url", "data:image/jpeg;base64,$base64Image")
        imageContent.put("image_url", imageUrl)
        contentArray.put(imageContent)

        message.put("content", contentArray)

        val messagesArray = JSONArray()
        messagesArray.put(message)
        payload.put("messages", messagesArray)

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response from Groq")

        if (!response.isSuccessful) {
            throw Exception("Groq Error: ${response.code} - $responseBody")
        }

        // Extract assistant message
        val root = JSONObject(responseBody)
        val choices = root.getJSONArray("choices")
        val firstChoice = choices.getJSONObject(0)
        val messageObj = firstChoice.getJSONObject("message")
        val jsonString = messageObj.getString("content")

        val jsonLimpio = jsonString.replace("```json", "").replace("```", "").trim()
        val jsonParser = Json { ignoreUnknownKeys = true; explicitNulls = false; coerceInputValues = true }
        return@withContext jsonParser.decodeFromString<InvoiceData>(jsonLimpio)
    }
}

fun generarResumenLimpio(datos: InvoiceData, lineasEspaciado: Int = 1): String {
    val space = "\n".repeat(lineasEspaciado)
    val builder = StringBuilder()

    datos.proveedor?.let { builder.append("Proveedor: $it").append(space) }
    datos.sucursal?.let { builder.append("Sucursal: $it").append(space) }
    datos.fecha?.let { builder.append("Fecha: $it").append(space) }
    datos.hora?.let { builder.append("Hora: $it").append(space) }
    datos.regimen_fiscal?.let { builder.append("Régimen Fiscal: $it").append(space) }

    if (!datos.articulos.isNullOrEmpty()) {
        builder.append("Artículos:").append(space)
        datos.articulos.forEach { art ->
            val cant = art.cantidad?.toString() ?: "1"
            val desc = art.descripcion ?: "Desconocido"
            val imp = art.importe?.toString() ?: "0.0"
            builder.append("- $cant x $desc - $$imp").append(space)
        }
    }

    datos.subtotal?.let { builder.append("Subtotal: $$it").append(space) }
    datos.iva?.let { builder.append("IVA: $$it").append(space) }
    datos.ieps?.let { builder.append("IEPS: $$it").append(space) }
    if (!datos.impuestos.isNullOrEmpty()) {
        builder.append("Impuestos:").append(space)
        datos.impuestos.forEach { imp ->
            val tipo = imp.tipo ?: "Desconocido"
            val monto = imp.monto?.toString() ?: "0.0"
            builder.append("- $tipo: $$monto").append(space)
        }
    }
    datos.total?.let { builder.append("Total: $$it").append(space) }
    datos.metodo_pago?.let { builder.append("Método de Pago: $it").append(space) }
    datos.link_facturacion?.let { builder.append("Link de Facturación: $it").append(space) }

    return builder.toString().trimEnd()
}

fun formatearTicketLimpio(jsonIA: String?, lineasEspaciado: Int = 1): String {
    if (jsonIA.isNullOrBlank()) {
        return "Error al procesar los datos limpios"
    }
    return try {
        val jsonParser = kotlinx.serialization.json.Json { 
            ignoreUnknownKeys = true 
            explicitNulls = false 
            coerceInputValues = true 
        }
        val datos = jsonParser.decodeFromString<InvoiceData>(jsonIA)
        generarResumenLimpio(datos, lineasEspaciado)
    } catch (e: Exception) {
        "Error al procesar los datos limpios"
    }
}