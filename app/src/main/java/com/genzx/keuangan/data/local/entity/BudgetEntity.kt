package com.genzx.keuangan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val limitAmount: Double,
    val month: Int, // 1-12
    val year: Int,
    val createdAt: String = ""
)
