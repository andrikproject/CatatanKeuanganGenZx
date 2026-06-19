package com.genzx.keuangan.data.repository

import com.genzx.keuangan.data.local.dao.AccountDao
import com.genzx.keuangan.data.local.dao.BudgetDao
import com.genzx.keuangan.data.local.dao.TransactionDao
import com.genzx.keuangan.data.local.entity.AccountEntity
import com.genzx.keuangan.data.local.entity.BudgetEntity
import com.genzx.keuangan.data.local.entity.TransactionEntity
import com.genzx.keuangan.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class FinancialRepository(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao
) {

    // ── Transactions ──────────────────────────────────────────────────────────

    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { list ->
            list.map { it.toDomain() }
        }

    fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>> {
        val monthYear = "%04d-%02d".format(year, month)
        return transactionDao.getTransactionsByMonth(monthYear).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTransactionsByYear(year: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsByYear(year.toString()).map { list ->
            list.map { it.toDomain() }
        }

    fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query).map { list ->
            list.map { it.toDomain() }
        }

    fun getTotalIncomeByMonth(year: Int, month: Int): Flow<Double> {
        val monthYear = "%04d-%02d".format(year, month)
        return transactionDao.getTotalIncomeByMonth(monthYear).map { it ?: 0.0 }
    }

    fun getTotalExpenseByMonth(year: Int, month: Int): Flow<Double> {
        val monthYear = "%04d-%02d".format(year, month)
        return transactionDao.getTotalExpenseByMonth(monthYear).map { it ?: 0.0 }
    }

    suspend fun insertTransaction(transaction: Transaction, updateBalance: Boolean = true): Long {
        val entity = transaction.toEntity()
        val id = transactionDao.insertTransaction(entity)
        if (updateBalance) {
            val delta = if (transaction.type == TransactionType.INCOME) transaction.amount else -transaction.amount
            accountDao.updateBalance(transaction.accountId, delta)
        }
        return id
    }

    suspend fun updateTransaction(old: Transaction, new: Transaction) {
        // Reverse old balance effect
        val oldDelta = if (old.type == TransactionType.INCOME) -old.amount else old.amount
        accountDao.updateBalance(old.accountId, oldDelta)
        // Apply new balance effect
        val newDelta = if (new.type == TransactionType.INCOME) new.amount else -new.amount
        accountDao.updateBalance(new.accountId, newDelta)
        transactionDao.updateTransaction(new.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        // Reverse balance
        val delta = if (transaction.type == TransactionType.INCOME) -transaction.amount else transaction.amount
        accountDao.updateBalance(transaction.accountId, delta)
        transactionDao.deleteTransactionById(transaction.id)
    }

    // ── Accounts ──────────────────────────────────────────────────────────────

    fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAllAccounts().map { list -> list.map { it.toDomain() } }

    fun getTotalBalance(): Flow<Double> =
        accountDao.getTotalBalance().map { it ?: 0.0 }

    suspend fun insertAccount(account: Account): Long =
        accountDao.insertAccount(account.toEntity())

    suspend fun updateAccount(account: Account) =
        accountDao.updateAccount(account.toEntity())

    suspend fun deleteAccount(account: Account) =
        accountDao.deleteAccount(account.toEntity())

    suspend fun setDefaultAccount(accountId: Long) {
        accountDao.clearDefaultAccount()
        accountDao.setDefaultAccount(accountId)
    }

    suspend fun transferBetweenAccounts(fromId: Long, toId: Long, amount: Double) {
        accountDao.updateBalance(fromId, -amount)
        accountDao.updateBalance(toId, amount)
    }

    // ── Budgets ───────────────────────────────────────────────────────────────

    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> =
        budgetDao.getBudgetsByMonth(month, year).map { list -> list.map { it.toDomain() } }

    suspend fun insertBudget(budget: Budget): Long =
        budgetDao.insertBudget(budget.toEntity())

    suspend fun updateBudget(budget: Budget) =
        budgetDao.updateBudget(budget.toEntity())

    suspend fun deleteBudget(budget: Budget) =
        budgetDao.deleteBudget(budget.toEntity())
}

// ── Mappers ───────────────────────────────────────────────────────────────────

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    title = title,
    amount = amount,
    type = if (type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE,
    category = category,
    accountId = accountId,
    note = note,
    date = date,
    createdAt = createdAt
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    type = type.name,
    category = category,
    accountId = accountId,
    note = note,
    date = date,
    createdAt = createdAt.ifEmpty { LocalDateTime.now().toString() }
)

fun AccountEntity.toDomain() = Account(
    id = id,
    name = name,
    type = AccountType.valueOf(type),
    balance = balance,
    color = color,
    icon = icon,
    isDefault = isDefault,
    createdAt = createdAt
)

fun Account.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = type.name,
    balance = balance,
    color = color,
    icon = icon,
    isDefault = isDefault,
    createdAt = createdAt.ifEmpty { LocalDateTime.now().toString() }
)

fun BudgetEntity.toDomain() = Budget(
    id = id,
    category = category,
    limitAmount = limitAmount,
    month = month,
    year = year
)

fun Budget.toEntity() = BudgetEntity(
    id = id,
    category = category,
    limitAmount = limitAmount,
    month = month,
    year = year,
    createdAt = LocalDateTime.now().toString()
)
