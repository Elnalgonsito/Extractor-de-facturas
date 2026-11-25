package com.tecnoayuda.extractorfacturas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecnoayuda.extractorfacturas.data.local.Folder
import com.tecnoayuda.extractorfacturas.data.local.Invoice
import com.tecnoayuda.extractorfacturas.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvoiceViewModel(
    private val repository: InvoiceRepository
) : ViewModel() {

    val invoiceList: StateFlow<List<Invoice>> = repository.getAllInvoices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folderList: StateFlow<List<Folder>> = _folders.asStateFlow()

    fun loadFolders(type: String) {
        viewModelScope.launch {
            repository.getFoldersByType(type).collect {
                _folders.value = it
            }
        }
    }

    fun getInvoicesForFolder(folderId: Long): StateFlow<List<Invoice>> {
        return repository.getInvoicesByFolder(folderId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun createFolder(name: String, documentType: String) {
        viewModelScope.launch {
            repository.insertFolder(Folder(name = name, documentType = documentType))
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.deleteFolder(folder)
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
        }
    }

    fun saveInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.insertInvoice(invoice)
        }
    }
}

class InvoiceViewModelFactory(private val repository: InvoiceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoiceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvoiceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
