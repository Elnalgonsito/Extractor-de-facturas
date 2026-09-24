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
    val correoDestino: String? = null
)
