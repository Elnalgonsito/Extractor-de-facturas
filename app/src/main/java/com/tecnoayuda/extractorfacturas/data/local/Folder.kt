package com.tecnoayuda.extractorfacturas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val documentType: String
)
