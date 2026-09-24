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
import coil.compose.AsyncImage
import com.tecnoayuda.extractorfacturas.GeminiInvoiceParser
import com.tecnoayuda.extractorfacturas.utils.extraerDatosDelTicket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceConfirmationScreen(
    initialExtractedText: String? = null,
    initialImagenUri: String? = null,
    initialProvider: String = "",
    initialDate: String = "",
    initialTotal: String = "",
    initialLinkFactura: String? = null,
    initialCorreoDestino: String? = null,
    modifier: Modifier = Modifier,
    onSaveClick: (provider: String, date: String, total: String, linkFactura: String?, correoDestino: String?, imagenUri: String?, textoCrudo: String?) -> Unit,
    onBackClick: () -> Unit
) {
    var provider by remember(initialProvider) { mutableStateOf(initialProvider) }
    var date by remember(initialDate) { mutableStateOf(initialDate) }
    var total by remember(initialTotal) { mutableStateOf(initialTotal) }
    var linkFactura by remember(initialLinkFactura) { mutableStateOf(initialLinkFactura ?: "") }
    var correoDestino by remember(initialCorreoDestino) { mutableStateOf(initialCorreoDestino ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var extractedText by remember(initialExtractedText) { mutableStateOf(initialExtractedText ?: "") }

    val parser = remember { GeminiInvoiceParser() }
    val context = LocalContext.current

    LaunchedEffect(initialExtractedText) {
        if (!initialExtractedText.isNullOrBlank()) {
            isLoading = true
            // Regex fallback initially (optional)
            val regexData = extraerDatosDelTicket(initialExtractedText)
            if (date.isBlank() && regexData.date != null) date = regexData.date
            if (total.isBlank() && regexData.total != null) total = regexData.total
            
            // AI parsing
            val parsedData = parser.parseInvoice(initialExtractedText)
            if (parsedData != null) {
                if (!parsedData.proveedor.isNullOrBlank()) provider = parsedData.proveedor
                if (!parsedData.fecha_hora.isNullOrBlank()) date = parsedData.fecha_hora
                if (parsedData.total != null) total = parsedData.total.toString()
                if (!parsedData.link_facturacion.isNullOrBlank()) linkFactura = parsedData.link_facturacion
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
                        .height(200.dp),
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
                value = total,
                onValueChange = { total = it },
                label = { Text("Total Amount") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.AttachMoney, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            
            if (extractedText.isNotBlank()) {
                OutlinedTextField(
                    value = extractedText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Extracted Text (Raw)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = MaterialTheme.shapes.large,
                    maxLines = 5
                )
            }

            OutlinedTextField(
                value = linkFactura,
                onValueChange = { linkFactura = it },
                label = { Text("Link para facturar") },
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
                    onSaveClick(
                        provider,
                        date,
                        total,
                        linkFactura.takeIf { it.isNotBlank() },
                        correoDestino.takeIf { it.isNotBlank() },
                        initialImagenUri,
                        extractedText.takeIf { it.isNotBlank() }
                    )
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
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(correoDestino))
                        putExtra(Intent.EXTRA_SUBJECT, "Datos de Facturación - $provider")
                        val body = """
                            Proveedor: $provider
                            Fecha: $date
                            Total: $total
                            Link para facturar: $linkFactura
                            
                            Texto Original:
                            $extractedText
                        """.trimIndent()
                        putExtra(Intent.EXTRA_TEXT, body)
                    }
                    try {
                        context.startActivity(intent)
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
}

@Preview
@Composable
fun InvoiceConfirmationScreenPreview() {
    MaterialTheme {
        InvoiceConfirmationScreen(
            onSaveClick = { _, _, _, _, _, _, _ -> },
            onBackClick = {}
        )
    }
}
