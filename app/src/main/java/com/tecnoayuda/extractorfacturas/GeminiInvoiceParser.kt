package com.tecnoayuda.extractorfacturas

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import kotlin.math.abs
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
    val link_facturacion: String? = null,
    val facturaExtraida: FacturaExtraida? = null
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

@Serializable
data class Emisor(
    val rfc: String? = null,
    val razonSocial: String,
    val regimenFiscal: String? = null
)

@Serializable
data class Receptor(
    val rfc: String? = null,
    val razonSocial: String? = null,
    val usoCfdi: String? = null
)

@Serializable
data class ConceptoFactura(
    val cantidad: Double = 0.0,
    val unidad: String? = null,
    val claveProdServ: String? = null,
    val descripcion: String = "",
    val valorUnitario: Double = 0.0,
    val importe: Double = 0.0,
    val tasaIva: Double = 0.0,
    val montoIva: Double = 0.0,
    val tasaIeps: Double = 0.0,
    val montoIeps: Double = 0.0
)

@Serializable
data class FacturaExtraida(
    val uuid: String? = null,
    val serie: String? = null,
    val folio: String? = null,
    val fechaEmision: String = "",
    val tipoComprobante: String? = null,
    val lugarExpedicion: String? = null,
    
    val emisor: Emisor? = null,
    val receptor: Receptor? = null,
    val conceptos: List<ConceptoFactura>? = null,
    
    val subtotal: Double = 0.0,
    val descuentos: Double = 0.0,
    val ivaTotal: Double = 0.0,
    val iepsTotal: Double = 0.0,
    val retencionesIsr: Double = 0.0,
    val retencionesIva: Double = 0.0,
    val total: Double = 0.0,
    val moneda: String = "MXN",
    val tipoCambio: Double = 1.0,
    val formaPago: String? = null,
    val metodoPago: String? = null,
    val link_facturacion: String? = null
) {
    fun validarCoherenciaMatematica(): Boolean {
        val sumaEsperada = subtotal - descuentos + ivaTotal + iepsTotal - retencionesIsr - retencionesIva
        return abs(sumaEsperada - total) <= 0.05
    }
}

const val SYSTEM_PROMPT_FACTURAS = """Eres un sistema experto en extracción de datos de CFDI (Facturas Mexicanas). Analiza la imagen y extrae la información solicitada.
Reglas estables:
- Extrae el UUID (folio fiscal 36 caracteres), si no hay, usa null.
- Fecha en formato YYYY-MM-DDTHH:mm:ss o YYYY-MM-DD.
- Limpia símbolos de moneda ($) y comas (,). Devuelve Doubles puros.
- Desglosa cada concepto con su IVA (16% u 8% fronterizo) e IEPS si aplica. Si no, pon 0.0.
- Asigna 0.0 a montos no presentes. No inventes datos.
Devuelve ÚNICAMENTE un JSON válido que cumpla esta estructura exacta (sin Markdown): 
{ "uuid": "...", "serie": "...", "folio": "...", "fechaEmision": "...", "tipoComprobante": "...", "lugarExpedicion": "...", "emisor": { "rfc": "...", "razonSocial": "...", "regimenFiscal": "..." }, "receptor": { "rfc": "...", "razonSocial": "...", "usoCfdi": "..." }, "conceptos": [ { "cantidad": 0.0, "unidad": "...", "claveProdServ": "...", "descripcion": "...", "valorUnitario": 0.0, "importe": 0.0, "tasaIva": 0.0, "montoIva": 0.0, "tasaIeps": 0.0, "montoIeps": 0.0 } ], "subtotal": 0.0, "descuentos": 0.0, "ivaTotal": 0.0, "iepsTotal": 0.0, "retencionesIsr": 0.0, "retencionesIva": 0.0, "total": 0.0, "moneda": "MXN", "tipoCambio": 1.0, "formaPago": "...", "metodoPago": "...", "link_facturacion": "..." }"""

class GeminiInvoiceParser {

    private val client = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    suspend fun parseInvoice(bitmap: Bitmap, documentType: String): InvoiceData = withContext(Dispatchers.IO) {
        val promptText = if (documentType == "Factura") SYSTEM_PROMPT_FACTURAS else """
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

        // Extract QR Code Link if available
        val qrLink = scanQrCode(bitmap)

        // Build JSON payload
        val payload = JSONObject()
        payload.put("model", "qwen/qwen3.8-27b")
        payload.put("max_tokens", 800) // Limita estrictamente la salida para no romper la cuota gratuita
        payload.put("temperature", 0.1)
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

        var currentAttempt = 0
        val maxAttempts = 3
        var finalParsedData: InvoiceData? = null
        
        while (true) {
            try {
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
                
                finalParsedData = if (documentType == "Factura") {
                    val factura = jsonParser.decodeFromString<FacturaExtraida>(jsonLimpio)
                    // val esMatematicamenteValida = factura.validarCoherenciaMatematica()
                    InvoiceData(
                        proveedor = factura.emisor?.razonSocial,
                        fecha = factura.fechaEmision,
                        subtotal = factura.subtotal,
                        iva = factura.ivaTotal,
                        ieps = factura.iepsTotal,
                        total = factura.total,
                        link_facturacion = factura.link_facturacion,
                        metodo_pago = factura.metodoPago,
                        regimen_fiscal = factura.emisor?.regimenFiscal,
                        facturaExtraida = factura
                    )
                } else {
                    jsonParser.decodeFromString<InvoiceData>(jsonLimpio)
                }
                break
            } catch (e: Exception) {
                currentAttempt++
                if (currentAttempt >= maxAttempts) throw e
                kotlinx.coroutines.delay(5000L) // Wait 5s before retrying connection resets
            }
        }
        
        val parsedData = finalParsedData ?: throw Exception("Failed to parse invoice")
        
        return@withContext if (!qrLink.isNullOrBlank()) {
            parsedData.copy(link_facturacion = qrLink)
        } else {
            parsedData
        }
    }
}

suspend fun scanQrCode(bitmap: android.graphics.Bitmap): String? = suspendCancellableCoroutine { continuation ->
    val image = InputImage.fromBitmap(bitmap, 0)
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val scanner = BarcodeScanning.getClient(options)

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            // Try to get a URL, otherwise get raw value
            val url = barcodes.firstOrNull()?.url?.url ?: barcodes.firstOrNull()?.rawValue
            if (continuation.isActive) continuation.resume(url)
        }
        .addOnFailureListener {
            if (continuation.isActive) continuation.resume(null)
        }
        .addOnCompleteListener {
            scanner.close()
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

fun generarResumenFactura(factura: FacturaExtraida, lineasEspaciado: Int = 1): String {
    val space = "\n".repeat(lineasEspaciado)
    val b = StringBuilder()
    factura.uuid?.let { b.append("UUID: $it").append(space) }
    b.append("Emisor: ${factura.emisor?.razonSocial ?: "N/A"} (${factura.emisor?.rfc ?: "N/A"})").append(space)
    b.append("Receptor: ${factura.receptor?.razonSocial ?: "N/A"} (${factura.receptor?.rfc ?: "N/A"})").append(space)
    factura.receptor?.usoCfdi?.let { b.append("Uso CFDI: $it").append(space) }
    b.append("Fecha: ${factura.fechaEmision}").append(space)
    
    if (!factura.conceptos.isNullOrEmpty()) {
        b.append("Conceptos:").append(space)
        factura.conceptos.forEach { c ->
            b.append("- ${c.cantidad}x ${c.descripcion} - $${c.importe}").append(space)
        }
    }
    
    b.append("Subtotal: $${factura.subtotal}").append(space)
    b.append("IVA: $${factura.ivaTotal}").append(space)
    b.append("IEPS: $${factura.iepsTotal}").append(space)
    b.append("Total: $${factura.total}").append(space)
    
    factura.formaPago?.let { b.append("Forma Pago: $it").append(space) }
    factura.metodoPago?.let { b.append("Método Pago: $it").append(space) }
    
    return b.toString().trimEnd()
}