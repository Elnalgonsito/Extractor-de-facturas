package com.tecnoayuda.extractorfacturas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecnoayuda.extractorfacturas.data.local.Folder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderListScreen(
    documentType: String,
    folders: List<Folder>,
    onCreateFolder: (String, String) -> Unit,
    onFolderClick: (Folder) -> Unit,
    onDeleteFolder: (Folder) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carpetas de $documentType") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Folder")
            }
        }
    ) { padding ->
        if (folders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No folders yet. Create one!")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(folders) { folder ->
                    ListItem(
                        headlineContent = { Text(folder.name) },
                        leadingContent = {
                            Icon(Icons.Default.Folder, contentDescription = null)
                        },
                        trailingContent = {
                            IconButton(onClick = { onDeleteFolder(folder) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Folder")
                            }
                        },
                        modifier = Modifier.clickable { onFolderClick(folder) }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Create Folder") },
                text = {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Folder Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newFolderName.isNotBlank()) {
                                onCreateFolder(newFolderName, documentType)
                            }
                            showDialog = false
                            newFolderName = ""
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        newFolderName = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
