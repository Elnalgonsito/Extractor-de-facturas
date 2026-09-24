package com.tecnoayuda.extractorfacturas.data.repository

import com.tecnoayuda.extractorfacturas.data.local.Invoice
import com.tecnoayuda.extractorfacturas.data.local.InvoiceDao
import kotlinx.coroutines.flow.Flow

class InvoiceRepositoryImpl(
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {

    override fun getAllInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getAllInvoices()
    }

    override suspend fun getInvoiceById(id: Long): Invoice? {
        return invoiceDao.getInvoiceById(id)
    }

    override suspend fun insertInvoice(invoice: Invoice): Long {
        return invoiceDao.insertInvoice(invoice)
    }

    override suspend fun updateInvoice(invoice: Invoice) {
        invoiceDao.updateInvoice(invoice)
    }

    override suspend fun deleteInvoice(invoice: Invoice) {
        invoiceDao.deleteInvoice(invoice)
    }
}
