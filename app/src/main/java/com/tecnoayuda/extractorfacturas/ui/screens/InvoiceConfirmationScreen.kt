package com.tecnoayuda.extractorfacturas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.rounded.Close
import coil.compose.AsyncImage
import com.tecnoayuda.extractorfacturas.GeminiInvoiceParser
import com.tecnoayuda.extractorfacturas.InvoiceData
import com.tecnoayuda.extractorfacturas.formatearTicketLimpio
import com.tecnoayuda.extractorfacturas.generarResumenFactura
import com.tecnoayuda.extractorfacturas.data.local.Invoice


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceConfirmationScreen(
    invoiceId: Long = 0L,
    folderId: Long = 0L,
    documentType: String = "Ticket",
    initialExtractedText: String? = null,
    initialImagenUri: String? = null,
    initialProvider: String = "",
    initialDate: String = "",
    initialTotal: String = "",
    initialSubtotal: String = "",
    initialIva: String = "",
    initialIeps: String = "",
    initialLinkFactura: String? = null,
    initialCorreoDestino: String? = null,
    initialUuid: String? = null,
    initialRfcEmisor: String? = null,
    initialRfcReceptor: String? = null,
    initialUsoCfdi: String? = null,
    initialFormaPago: String? = null,
    initialMetodoPago: String? = null,
    modifier: Modifier = Modifier,
    onDeleteClick: (() -> Unit)? = null,
    onSaveClick: (Invoice) -> Unit,
    onBackClick: () -> Unit
) {
    var provider by remember(initialProvider) { mutableStateOf(initialProvider) }
    var date by remember(initialDate) { mutableStateOf(initialDate) }
    var total by remember(initialTotal) { mutableStateOf(initialTotal) }
    var subtotal by remember(initialSubtotal) { mutableStateOf(initialSubtotal) }
    var iva by remember(initialIva) { mutableStateOf(initialIva) }
    var ieps by remember(initialIeps) { mutableStateOf(initialIeps) }
    var linkFactura by remember(initialLinkFactura) { mutableStateOf(initialLinkFactura ?: "") }
    var correoDestino by remember(initialCorreoDestino) { mutableStateOf(initialCorreoDestino ?: "") }
    
    var uuid by remember(initialUuid) { mutableStateOf(initialUuid ?: "") }
    var rfcEmisor by remember(initialRfcEmisor) { mutableStateOf(initialRfcEmisor ?: "") }
    var rfcReceptor by remember(initialRfcReceptor) { mutableStateOf(initialRfcReceptor ?: "") }
    var usoCfdi by remember(initialUsoCfdi) { mutableStateOf(initialUsoCfdi ?: "") }
    var formaPago by remember(initialFormaPago) { mutableStateOf(initialFormaPago ?: "") }
    var metodoPago by remember(initialMetodoPago) { mutableStateOf(initialMetodoPago ?: "") }
    var razonSocialEmisor by remember { mutableStateOf("") }
    var razonSocialReceptor by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var extractedText by remember(initialExtractedText) { mutableStateOf(initialExtractedText ?: "") }
    var currentInvoiceData by remember { mutableStateOf<InvoiceData?>(null) }
    var parsingStatus by remember { mutableStateOf("Analizando con Groq Vision...") }
    var isImageExpanded by remember { mutableStateOf(false) }

    val parser = remember { GeminiInvoiceParser() }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(initialImagenUri) {
        if (!initialImagenUri.isNullOrBlank()) {
            isLoading = true
            
            try {
                val uri = android.net.Uri.parse(initialImagenUri)
                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = android.graphics.ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    val maxDim = maxOf(info.size.width, info.size.height)
                    if (maxDim > 1024) {
                        decoder.setTargetSampleSize(maxDim / 1024)
                    }
                }
                
                // AI parsing
                val parsedData = parser.parseInvoice(bitmap, documentType)
                currentInvoiceData = parsedData
                
                if (documentType == "Factura") {
                    val f = parsedData.facturaExtraida
                    if (f != null) {
                        if (!f.emisor?.razonSocial.isNullOrBlank()) provider = f.emisor?.razonSocial ?: ""
                        if (!f.fechaEmision.isNullOrBlank()) date = f.fechaEmision
                        total = f.total.toString()
                        subtotal = f.subtotal.toString()
                        iva = f.ivaTotal.toString()
                        ieps = f.iepsTotal.toString()
                        
                        // specific factura fields
                        uuid = f.uuid ?: ""
                        rfcEmisor = f.emisor?.rfc ?: ""
                        razonSocialEmisor = f.emisor?.razonSocial ?: ""
                        rfcReceptor = f.receptor?.rfc ?: ""
                        razonSocialReceptor = f.receptor?.razonSocial ?: ""
                        usoCfdi = f.receptor?.usoCfdi ?: ""
                        formaPago = f.formaPago ?: ""
                        metodoPago = f.metodoPago ?: ""
                    }
                } else {
                    if (!parsedData.proveedor.isNullOrBlank()) provider = parsedData.proveedor
                    if (!parsedData.fecha.isNullOrBlank()) date = parsedData.fecha
                    if (parsedData.total != null) total = parsedData.total.toString()
                    if (parsedData.subtotal != null) subtotal = parsedData.subtotal.toString()
                    if (parsedData.iva != null) iva = parsedData.iva.toString()
                    if (parsedData.ieps != null) ieps = parsedData.ieps.toString()
                }
                
                if (!parsedData.link_facturacion.isNullOrBlank()) linkFactura = parsedData.link_facturacion
            } catch (e: Throwable) {
                val rawError = e.localizedMessage ?: e.javaClass.simpleName
                val errorMsg = when {
                    rawError.contains("503") || rawError.contains("high demand") -> 
                        "Los servidores de Groq están saturados en este momento. Por favor, intenta de nuevo en unos minutos."
                    rawError.contains("MissingFieldException") -> 
                        "Error de comunicación con la API (Posible saturación). Intenta de nuevo más tarde."
                    else -> "Error de procesamiento: $rawError"
                }
                provider = errorMsg
                date = errorMsg
                total = errorMsg
                parsingStatus = errorMsg
            }
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Invoice Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (onDeleteClick != null) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete Invoice",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Confirm details",
                style = MaterialTheme.typography.headlineMedium
            )
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (!initialImagenUri.isNullOrBlank()) {
                AsyncImage(
                    model = initialImagenUri,
                    contentDescription = "Invoice Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { isImageExpanded = true },
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = { Text("Provider") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Storefront, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.CalendarToday, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            OutlinedTextField(
                value = subtotal,
                onValueChange = { subtotal = it },
                label = { Text("Subtotal") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.AttachMoney, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            OutlinedTextField(
                value = iva,
                onValueChange = { iva = it },
                label = { Text("IVA") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.AttachMoney, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            OutlinedTextField(
                value = ieps,
                onValueChange = { ieps = it },
                label = { Text("IEPS") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.AttachMoney, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            OutlinedTextField(
                value = total,
                onValueChange = { total = it },
                label = { Text("Total Amount") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.AttachMoney, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            
            if (documentType == "Factura") {
                OutlinedTextField(
                    value = uuid,
                    onValueChange = { uuid = it },
                    label = { Text("UUID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    minLines = 2,
                    maxLines = 3
                )
                OutlinedTextField(
                    value = rfcEmisor,
                    onValueChange = { rfcEmisor = it },
                    label = { Text("RFC Emisor") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
                OutlinedTextField(
                    value = rfcReceptor,
                    onValueChange = { rfcReceptor = it },
                    label = { Text("RFC Receptor") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
                OutlinedTextField(
                    value = usoCfdi,
                    onValueChange = { usoCfdi = it },
                    label = { Text("Uso CFDI") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
                OutlinedTextField(
                    value = formaPago,
                    onValueChange = { formaPago = it },
                    label = { Text("Forma Pago") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
                OutlinedTextField(
                    value = metodoPago,
                    onValueChange = { metodoPago = it },
                    label = { Text("Método Pago") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )
            }
            
            val textToDisplay = if (documentType == "Factura" && currentInvoiceData?.facturaExtraida != null) {
                generarResumenFactura(currentInvoiceData!!.facturaExtraida!!)
            } else {
                val jsonGeneradoPorIA = currentInvoiceData?.let {
                    kotlinx.serialization.json.Json.encodeToString(com.tecnoayuda.extractorfacturas.InvoiceData.serializer(), it)
                }
                jsonGeneradoPorIA?.let { formatearTicketLimpio(it, 1) } ?: parsingStatus
            }
            if (textToDisplay.isNotEmpty()) {
                Text(
                    text = "Resumen:\n\n$textToDisplay",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            OutlinedTextField(
                value = linkFactura,
                onValueChange = { linkFactura = it },
                label = { 
                    Text(if (documentType == "Factura") "Enlace de Verificación (SAT)" else "Link para facturar") 
                },
                trailingIcon = {
                    if (linkFactura.isNotBlank()) {
                        IconButton(onClick = {
                            try {
                                uriHandler.openUri(linkFactura)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }) {
                            Icon(imageVector = Icons.Rounded.OpenInNew, contentDescription = "Abrir enlace")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            OutlinedTextField(
                value = correoDestino,
                onValueChange = { correoDestino = it },
                label = { Text("Correo de destino") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))

            Button(
                onClick = {
                    val invoice = Invoice(
                        id = invoiceId,
                        folderId = folderId,
                        date = date,
                        provider = provider,
                        totalAmount = total.toDoubleOrNull() ?: 0.0,
                        imagenUri = initialImagenUri,
                        textoCrudo = extractedText.takeIf { it.isNotBlank() },
                        linkFactura = linkFactura.takeIf { it.isNotBlank() },
                        correoDestino = correoDestino.takeIf { it.isNotBlank() },
                        subtotal = subtotal.toDoubleOrNull(),
                        iva = iva.toDoubleOrNull(),
                        ieps = ieps.toDoubleOrNull(),
                        documentType = documentType,
                        uuid = uuid.takeIf { it.isNotBlank() },
                        rfcEmisor = rfcEmisor.takeIf { it.isNotBlank() },
                        rfcReceptor = rfcReceptor.takeIf { it.isNotBlank() },
                        usoCfdi = usoCfdi.takeIf { it.isNotBlank() },
                        formaPago = formaPago.takeIf { it.isNotBlank() },
                        metodoPago = metodoPago.takeIf { it.isNotBlank() }
                    )
                    onSaveClick(invoice)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Save Invoice", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822" // Forces email clients
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(correoDestino))
                        putExtra(Intent.EXTRA_SUBJECT, "Datos de Facturación - $provider")
                        
                        val emailBody = """
                            Datos de Facturación:
                            
                            Proveedor: $provider
                            Fecha: $date
                            
                            Subtotal: $$subtotal
                            IVA (16%): $$iva
                            IEPS (8%): $$ieps
                            Total: $$total
                            
                            Link para facturar: $linkFactura
                            
                            --------------------------
                            $textToDisplay
                        """.trimIndent()
                        
                        putExtra(Intent.EXTRA_TEXT, emailBody)
                        
                        // Attach image if present
                        if (!initialImagenUri.isNullOrBlank()) {
                            val uri = Uri.parse(initialImagenUri)
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Enviar correo con adjunto..."))
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text("Enviar por Correo", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (isImageExpanded && !initialImagenUri.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { isImageExpanded = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Ensures full screen
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                var scale by remember { mutableStateOf(1f) }
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }
                
                AsyncImage(
                    model = initialImagenUri,
                    contentDescription = "Full Screen Invoice",
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                val maxX = (size.width * (scale - 1)) / 2
                                val maxY = (size.height * (scale - 1)) / 2
                                offsetX = (offsetX + pan.x * scale).coerceIn(-maxX, maxX)
                                offsetY = (offsetY + pan.y * scale).coerceIn(-maxY, maxY)
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )
                
                IconButton(
                    onClick = { isImageExpanded = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun InvoiceConfirmationScreenPreview() {
    MaterialTheme {
        InvoiceConfirmationScreen(
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
