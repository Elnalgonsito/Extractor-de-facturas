package com.tecnoayuda.extractorfacturas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val provider: String,
    val totalAmount: Double,
    val imagenUri: String? = null,
    val textoCrudo: String? = null,
    val linkFactura: String? = null,
    val correoDestino: String? = null,
    val subtotal: Double? = null,
    val iva: Double? = null,
    val ieps: Double? = null,
    val folderId: Long = 0L,
    val documentType: String = "Ticket",
    val uuid: String? = null,
    val rfcEmisor: String? = null,
    val rfcReceptor: String? = null,
    val usoCfdi: String? = null,
    val formaPago: String? = null,
    val metodoPago: String? = null
)
