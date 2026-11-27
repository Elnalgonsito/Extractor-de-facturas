package com.tecnoayuda.extractorfacturas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecnoayuda.extractorfacturas.data.local.Invoice

import androidx.core.content.FileProvider
import java.io.File
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    folderName: String,
    invoices: List<Invoice>,
    onAddInvoiceClick: (String?, String?) -> Unit,
    onInvoiceClick: (Invoice) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempCameraUri?.let { uri ->
                onAddInvoiceClick(uri.toString(), "")
            }
        }
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onAddInvoiceClick("", uri.toString())
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(folderName, style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { showImageSourceDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Invoice"
                )
            }
        }
    ) { innerPadding ->
        if (invoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No invoices yet. Tap + to add.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(invoices, key = { it.id }) { invoice ->
                    InvoiceItemCard(
                        invoice = invoice,
                        onClick = { onInvoiceClick(invoice) }
                    )
                }
            }
        }
        
        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text("Agregar Documento") },
                text = { Text("Elige desde dónde quieres subir la imagen:") },
                confirmButton = {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        // Create a temporary file
                        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
                        val tempFile = File.createTempFile("CAPTURED_", ".jpg", imagesDir)
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
                        tempCameraUri = uri
                        takePictureLauncher.launch(uri)
                    }) {
                        Text("Cámara")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Text("Galería")
                    }
                }
            )
        }
    }
}

@Composable
fun InvoiceItemCard(
    invoice: Invoice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = invoice.provider,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Date: ${invoice.date}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Total: $${invoice.totalAmount}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun InvoiceListScreenPreview() {
    MaterialTheme {
        InvoiceListScreen(
            folderName = "General",
            invoices = listOf(
                Invoice(provider = "Supermarket", date = "2023-10-25", totalAmount = 120.50),
                Invoice(provider = "Internet Provider", date = "2023-10-26", totalAmount = 50.0)
            ),
            onAddInvoiceClick = { _, _ -> },
            onInvoiceClick = {}
        )
    }
}
