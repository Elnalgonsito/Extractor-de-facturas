package com.tecnoayuda.extractorfacturas.data.repository

import com.tecnoayuda.extractorfacturas.data.local.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    fun getAllInvoices(): Flow<List<Invoice>>
    suspend fun getInvoiceById(id: Long): Invoice?
    suspend fun insertInvoice(invoice: Invoice): Long
    suspend fun updateInvoice(invoice: Invoice)
    suspend fun deleteInvoice(invoice: Invoice)
}
