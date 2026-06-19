package com.genzx.keuangan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "CASH", "BANK", "SAVINGS", "EWALLET"
    val balance: Double = 0.0,
    val color: String = "#006B7A",
    val icon: String = "wallet",
    val isDefault: Boolean = false,
    val createdAt: String = ""
)
