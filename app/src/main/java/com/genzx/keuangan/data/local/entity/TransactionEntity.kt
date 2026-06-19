package com.genzx.keuangan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val accountId: Long,
    val note: String = "",
    val date: String, // ISO string: yyyy-MM-ddTHH:mm:ss
    val createdAt: String = LocalDateTime.now().toString()
)
