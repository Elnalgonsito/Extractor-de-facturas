package com.tecnoayuda.extractorfacturas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tecnoayuda.extractorfacturas.data.local.AppDatabase
import com.tecnoayuda.extractorfacturas.data.repository.InvoiceRepositoryImpl
import com.tecnoayuda.extractorfacturas.ui.screens.InvoiceConfirmationScreen
import com.tecnoayuda.extractorfacturas.ui.screens.InvoiceListScreen
import com.tecnoayuda.extractorfacturas.ui.screens.DocumentSelectorScreen
import com.tecnoayuda.extractorfacturas.ui.theme.ExtractorDeFacturasTheme
import com.tecnoayuda.extractorfacturas.ui.viewmodel.InvoiceViewModel
import com.tecnoayuda.extractorfacturas.ui.viewmodel.InvoiceViewModelFactory
import kotlinx.serialization.Serializable

@Serializable
object DocumentSelectorRoute : NavKey

@Serializable
data class FolderListRoute(val documentType: String) : NavKey

@Serializable
data class InvoiceListRoute(val folderId: Long, val folderName: String, val documentType: String) : NavKey

@Serializable
data class AddInvoiceRoute(val folderId: Long, val extractedText: String? = null, val imagenUri: String? = null, val documentType: String) : NavKey

@Serializable
data class InvoiceDetailRoute(val id: Long) : NavKey

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExtractorDeFacturasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { InvoiceRepositoryImpl(database.invoiceDao(), database.folderDao()) }
    val factory = remember { InvoiceViewModelFactory(repository) }
    val viewModel: InvoiceViewModel = viewModel(factory = factory)
    
    val folders by viewModel.folderList.collectAsState()
    val invoices by viewModel.invoiceList.collectAsState() // or load conditionally
    
    val backStack = rememberNavBackStack(DocumentSelectorRoute)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    
    NavDisplay(
        backStack = backStack,
        sceneStrategy = listDetailStrategy,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<DocumentSelectorRoute>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                DocumentSelectorScreen(
                    onSelectDocumentType = { type ->
                        backStack.add(FolderListRoute(type))
                    }
                )
            }

            entry<FolderListRoute>(
                metadata = ListDetailSceneStrategy.listPane()
            ) { route ->
                viewModel.loadFolders(route.documentType)
                com.tecnoayuda.extractorfacturas.ui.screens.FolderListScreen(
                    documentType = route.documentType,
                    folders = folders,
                    onCreateFolder = { name, type -> viewModel.createFolder(name, type) },
                    onFolderClick = { folder ->
                        backStack.add(InvoiceListRoute(folderId = folder.id, folderName = folder.name, documentType = folder.documentType))
                    },
                    onDeleteFolder = { folder -> viewModel.deleteFolder(folder) },
                    onBackClick = {
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    }
                )
            }

            entry<InvoiceListRoute>(
                metadata = ListDetailSceneStrategy.listPane()
            ) { route ->
                val folderInvoices = invoices.filter { it.folderId == route.folderId }
                InvoiceListScreen(
                    folderName = route.folderName,
                    invoices = folderInvoices,
                    onAddInvoiceClick = { text, uri -> backStack.add(AddInvoiceRoute(route.folderId, text, uri, route.documentType)) },
                    onInvoiceClick = { invoice ->
                        backStack.add(InvoiceDetailRoute(invoice.id))
                    },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
            
            entry<AddInvoiceRoute>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                InvoiceConfirmationScreen(
                    folderId = route.folderId,
                    documentType = route.documentType,
                    initialExtractedText = route.extractedText,
                    initialImagenUri = route.imagenUri,
                    onSaveClick = { invoice ->
                        viewModel.saveInvoice(invoice)
                        backStack.removeLastOrNull()
                    },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
            
            entry<InvoiceDetailRoute>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                val invoice = invoices.find { it.id == route.id }
                val folder = folders.find { it.id == invoice?.folderId }
                InvoiceConfirmationScreen(
                    invoiceId = invoice?.id ?: 0L,
                    folderId = invoice?.folderId ?: 0L,
                    documentType = folder?.documentType ?: "Ticket",
                    initialExtractedText = invoice?.textoCrudo,
                    initialImagenUri = invoice?.imagenUri,
                    initialProvider = invoice?.provider ?: "",
                    initialDate = invoice?.date ?: "",
                    initialTotal = invoice?.totalAmount?.toString() ?: "",
                    initialSubtotal = invoice?.subtotal?.toString() ?: "",
                    initialIva = invoice?.iva?.toString() ?: "",
                    initialIeps = invoice?.ieps?.toString() ?: "",
                    initialLinkFactura = invoice?.linkFactura,
                    initialCorreoDestino = invoice?.correoDestino,
                    initialUuid = invoice?.uuid,
                    initialRfcEmisor = invoice?.rfcEmisor,
                    initialRfcReceptor = invoice?.rfcReceptor,
                    initialUsoCfdi = invoice?.usoCfdi,
                    initialFormaPago = invoice?.formaPago,
                    initialMetodoPago = invoice?.metodoPago,
                    onDeleteClick = {
                        invoice?.let { viewModel.deleteInvoice(it) }
                        backStack.removeLastOrNull()
                    },
                    onSaveClick = { invoice ->
                        viewModel.saveInvoice(invoice)
                        backStack.removeLastOrNull()
                    },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
