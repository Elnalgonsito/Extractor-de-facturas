package com.tecnoayuda.extractorfacturas.data.repository

import com.tecnoayuda.extractorfacturas.data.local.Folder
import com.tecnoayuda.extractorfacturas.data.local.FolderDao
import com.tecnoayuda.extractorfacturas.data.local.Invoice
import com.tecnoayuda.extractorfacturas.data.local.InvoiceDao
import kotlinx.coroutines.flow.Flow

class InvoiceRepositoryImpl(
    private val invoiceDao: InvoiceDao,
    private val folderDao: FolderDao
) : InvoiceRepository {

    override fun getAllInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getAllInvoices()
    }

    override fun getInvoicesByFolder(folderId: Long): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesByFolder(folderId)
    }

    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders()
    }

    override suspend fun insertFolder(folder: Folder) {
        folderDao.insertFolder(folder)
    }

    override suspend fun deleteFolder(folder: Folder) {
        folderDao.deleteFolder(folder)
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
