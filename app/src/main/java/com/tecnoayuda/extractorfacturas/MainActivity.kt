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
import com.tecnoayuda.extractorfacturas.ui.theme.ExtractorDeFacturasTheme
import com.tecnoayuda.extractorfacturas.ui.viewmodel.InvoiceViewModel
import com.tecnoayuda.extractorfacturas.ui.viewmodel.InvoiceViewModelFactory
import kotlinx.serialization.Serializable

@Serializable
object InvoiceListRoute : NavKey

@Serializable
data class AddInvoiceRoute(val extractedText: String? = null, val imagenUri: String? = null) : NavKey

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
    val repository = remember { InvoiceRepositoryImpl(database.invoiceDao()) }
    val factory = remember { InvoiceViewModelFactory(repository) }
    val viewModel: InvoiceViewModel = viewModel(factory = factory)
    
    val invoices by viewModel.invoiceList.collectAsState()
    
    val backStack = rememberNavBackStack(InvoiceListRoute)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    
    NavDisplay(
        backStack = backStack,
        sceneStrategy = listDetailStrategy,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<InvoiceListRoute>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                InvoiceListScreen(
                    invoices = invoices,
                    onAddInvoiceClick = { text, uri -> backStack.add(AddInvoiceRoute(text, uri)) },
                    onInvoiceClick = { invoice ->
                        backStack.add(InvoiceDetailRoute(invoice.id))
                    }
                )
            }
            
            entry<AddInvoiceRoute>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                InvoiceConfirmationScreen(
                    initialExtractedText = route.extractedText,
                    initialImagenUri = route.imagenUri,
                    onSaveClick = { provider, date, total, linkFactura, correoDestino, imagenUri, textoCrudo ->
                        viewModel.saveInvoice(
                            date = date,
                            provider = provider,
                            totalAmount = total.toDoubleOrNull() ?: 0.0,
                            imagenUri = imagenUri,
                            textoCrudo = textoCrudo,
                            linkFactura = linkFactura,
                            correoDestino = correoDestino
                        )
                        backStack.removeLastOrNull()
                    },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
            
            entry<InvoiceDetailRoute>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                val invoice = invoices.find { it.id == route.id }
                InvoiceConfirmationScreen(
                    initialExtractedText = invoice?.textoCrudo,
                    initialImagenUri = invoice?.imagenUri,
                    initialProvider = invoice?.provider ?: "",
                    initialDate = invoice?.date ?: "",
                    initialTotal = invoice?.totalAmount?.toString() ?: "",
                    initialLinkFactura = invoice?.linkFactura,
                    initialCorreoDestino = invoice?.correoDestino,
                    onSaveClick = { provider, date, total, linkFactura, correoDestino, imagenUri, textoCrudo ->
                        // Since we don't have an update mechanism yet, we can just save as a new or ignore.
                        viewModel.saveInvoice(
                            date = date,
                            provider = provider,
                            totalAmount = total.toDoubleOrNull() ?: 0.0,
                            imagenUri = imagenUri,
                            textoCrudo = textoCrudo,
                            linkFactura = linkFactura,
                            correoDestino = correoDestino
                        )
                        backStack.removeLastOrNull()
                    },
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
