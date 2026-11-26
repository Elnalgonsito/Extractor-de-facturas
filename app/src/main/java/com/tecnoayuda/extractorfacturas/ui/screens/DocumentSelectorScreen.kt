package com.tecnoayuda.extractorfacturas.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentSelectorScreen(
    onSelectDocumentType: (String) -> Unit
) {
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Extractor de Documentos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) 
        }
    ) { innerPadding ->
        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { isVisible = true }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¿Qué deseas registrar?", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(48.dp))
                
                ElevatedCard(
                    onClick = { onSelectDocumentType("Ticket") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).animateContentSize(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.ReceiptLong, contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tickets", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ElevatedCard(
                    onClick = { onSelectDocumentType("Factura") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).animateContentSize(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.Description, contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Facturas", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}
