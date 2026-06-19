package com.genzx.keuangan.domain.model

data class Transaction(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val accountId: Long,
    val accountName: String = "",
    val note: String = "",
    val date: String,
    val createdAt: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val color: String = "#006B7A",
    val icon: String = "wallet",
    val isDefault: Boolean = false,
    val createdAt: String = ""
)

enum class AccountType {
    CASH, BANK, SAVINGS, EWALLET
}

data class Budget(
    val id: Long = 0,
    val category: String,
    val limitAmount: Double,
    val spentAmount: Double = 0.0,
    val month: Int,
    val year: Int
) {
    val remainingAmount: Double get() = limitAmount - spentAmount
    val progressPercent: Float get() = if (limitAmount > 0) (spentAmount / limitAmount).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget: Boolean get() = spentAmount > limitAmount
}

data class FinancialSummary(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netAmount: Double = totalIncome - totalExpense
)

data class CategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Float = 0f,
    val transactionCount: Int = 0
)

data class MonthlyData(
    val month: Int,
    val year: Int,
    val income: Double,
    val expense: Double
)

val EXPENSE_CATEGORIES = listOf(
    "Makanan & Minuman",
    "Transportasi",
    "Belanja",
    "Hiburan",
    "Kesehatan",
    "Pendidikan",
    "Tagihan & Utilitas",
    "Rumah",
    "Pakaian",
    "Olahraga",
    "Kecantikan",
    "Hadiah",
    "Investasi",
    "Lainnya"
)

val INCOME_CATEGORIES = listOf(
    "Gaji",
    "Freelance",
    "Bisnis",
    "Investasi",
    "Hadiah",
    "Bonus",
    "Lainnya"
)

val CATEGORY_ICONS = mapOf(
    "Makanan & Minuman" to "🍔",
    "Transportasi" to "🚗",
    "Belanja" to "🛍️",
    "Hiburan" to "🎮",
    "Kesehatan" to "💊",
    "Pendidikan" to "📚",
    "Tagihan & Utilitas" to "💡",
    "Rumah" to "🏠",
    "Pakaian" to "👕",
    "Olahraga" to "💪",
    "Kecantikan" to "💄",
    "Hadiah" to "🎁",
    "Investasi" to "📈",
    "Gaji" to "💰",
    "Freelance" to "💻",
    "Bisnis" to "🏢",
    "Bonus" to "🎯",
    "Lainnya" to "📦"
)
