package com.tecnoayuda.extractorfacturas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecnoayuda.extractorfacturas.data.local.Invoice
import com.tecnoayuda.extractorfacturas.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    fun saveInvoice(
        date: String,
        provider: String,
        totalAmount: Double,
        imagenUri: String?,
        textoCrudo: String?,
        linkFactura: String?,
        correoDestino: String?
    ) {
        val invoice = Invoice(
            date = date,
            provider = provider,
            totalAmount = totalAmount,
            imagenUri = imagenUri,
            textoCrudo = textoCrudo,
            linkFactura = linkFactura,
            correoDestino = correoDestino
        )
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
