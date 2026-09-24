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
    val fecha: String? = null,
    val hora: String? = null,
    val regimen_fiscal: String? = null,
    val metodo_pago: String? = null,
    val articulos: List<InvoiceItem>? = null,
    val subtotal: Double? = null,
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
            val prompt = "Eres una API estricta de extracción de datos contables. Tu única tarea es leer texto OCR, IGNORAR toda la basura (encuestas, www.miopinionwmx.com, beneficios, puntos, números 800, agradecimientos) y extraer solo los datos fiscales. Devuelve ÚNICAMENTE un JSON válido, sin etiquetas Markdown (sin ```json). Estructura obligatoria: { \"proveedor\": \"\", \"sucursal\": \"\", \"fecha\": \"YYYY-MM-DD\", \"hora\": \"HH:MM\", \"regimen_fiscal\": \"\", \"metodo_pago\": \"\", \"articulos\": [{\"cantidad\": 0.0, \"descripcion\": \"\", \"importe\": 0.0}], \"subtotal\": 0.0, \"impuestos\": [{\"tipo\": \"\", \"monto\": 0.0}], \"total\": 0.0, \"link_facturacion\": \"\" }. Si un dato no existe, pon null. Texto: $rawText"

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
        val jsonParser = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        val datos = jsonParser.decodeFromString<InvoiceData>(jsonIA)
        generarResumenLimpio(datos, lineasEspaciado)
    } catch (e: Exception) {
        "Error al procesar los datos limpios"
    }
}
