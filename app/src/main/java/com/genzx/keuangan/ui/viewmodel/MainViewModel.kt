package com.genzx.keuangan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.genzx.keuangan.data.local.database.AppDatabase
import com.genzx.keuangan.data.repository.FinancialRepository
import com.genzx.keuangan.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val isBalanceHidden: Boolean = false,
    val currentMonth: Int = LocalDateTime.now().monthValue,
    val currentYear: Int = LocalDateTime.now().year,
    val isLoading: Boolean = true
)

data class JurnalUiState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Semua", // Semua, Pemasukan, Pengeluaran
    val selectedMonth: Int = LocalDateTime.now().monthValue,
    val selectedYear: Int = LocalDateTime.now().year,
    val isLoading: Boolean = true
)

data class LaporanUiState(
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val categoryExpenses: List<CategorySummary> = emptyList(),
    val categoryIncomes: List<CategorySummary> = emptyList(),
    val monthlyData: List<MonthlyData> = emptyList(),
    val selectedMonth: Int = LocalDateTime.now().monthValue,
    val selectedYear: Int = LocalDateTime.now().year,
    val isLoading: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = FinancialRepository(
        db.transactionDao(),
        db.accountDao(),
        db.budgetDao()
    )

    // ── Home State ───────────────────────────────────────────────────────────
    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    // ── Jurnal State ──────────────────────────────────────────────────────────
    private val _jurnalState = MutableStateFlow(JurnalUiState())
    val jurnalState: StateFlow<JurnalUiState> = _jurnalState.asStateFlow()

    // ── Laporan State ──────────────────────────────────────────────────────────
    private val _laporanState = MutableStateFlow(LaporanUiState())
    val laporanState: StateFlow<LaporanUiState> = _laporanState.asStateFlow()

    // ── Accounts ──────────────────────────────────────────────────────────────
    val accounts: StateFlow<List<Account>> = repository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Selected Transaction for Edit ─────────────────────────────────────
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    init {
        loadHomeData()
        loadJurnalData()
        loadLaporanData()
    }

    // ── Home ───────────────────────────────────────────────────────────────────
    private fun loadHomeData() {
        val now = LocalDateTime.now()
        viewModelScope.launch {
            combine(
                repository.getTotalBalance(),
                repository.getTotalIncomeByMonth(now.year, now.monthValue),
                repository.getTotalExpenseByMonth(now.year, now.monthValue),
                repository.getAllTransactions(),
                repository.getAllAccounts()
            ) { balance, income, expense, transactions, accounts ->
                _homeState.update { state ->
                    state.copy(
                        totalBalance = balance,
                        monthlyIncome = income,
                        monthlyExpense = expense,
                        recentTransactions = transactions.take(5),
                        accounts = accounts,
                        isLoading = false
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            repository.getBudgetsByMonth(now.monthValue, now.year).collect { budgets ->
                _homeState.update { it.copy(budgets = budgets) }
            }
        }
    }

    fun toggleBalanceVisibility() {
        _homeState.update { it.copy(isBalanceHidden = !it.isBalanceHidden) }
    }

    // ── Jurnal ────────────────────────────────────────────────────────────────
    private fun loadJurnalData() {
        viewModelScope.launch {
            val state = _jurnalState.value
            repository.getTransactionsByMonth(state.selectedYear, state.selectedMonth)
                .collect { transactions ->
                    _jurnalState.update {
                        it.copy(
                            transactions = transactions,
                            filteredTransactions = applyJurnalFilter(transactions, it.selectedFilter, it.searchQuery),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun setJurnalMonth(month: Int, year: Int) {
        _jurnalState.update { it.copy(selectedMonth = month, selectedYear = year, isLoading = true) }
        loadJurnalData()
    }

    fun setJurnalFilter(filter: String) {
        _jurnalState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredTransactions = applyJurnalFilter(state.transactions, filter, state.searchQuery)
            )
        }
    }

    fun searchJurnal(query: String) {
        _jurnalState.update { state ->
            state.copy(
                searchQuery = query,
                filteredTransactions = applyJurnalFilter(state.transactions, state.selectedFilter, query)
            )
        }
    }

    private fun applyJurnalFilter(
        transactions: List<Transaction>,
        filter: String,
        query: String
    ): List<Transaction> {
        var result = transactions
        if (filter == "Pemasukan") result = result.filter { it.type == TransactionType.INCOME }
        if (filter == "Pengeluaran") result = result.filter { it.type == TransactionType.EXPENSE }
        if (query.isNotBlank()) result = result.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.note.contains(query, ignoreCase = true)
        }
        return result
    }

    // ── Laporan ───────────────────────────────────────────────────────────────
    private fun loadLaporanData() {
        val state = _laporanState.value
        viewModelScope.launch {
            combine(
                repository.getTotalIncomeByMonth(state.selectedYear, state.selectedMonth),
                repository.getTotalExpenseByMonth(state.selectedYear, state.selectedMonth),
                repository.getTransactionsByMonth(state.selectedYear, state.selectedMonth)
            ) { income, expense, transactions ->
                val expenseByCategory = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .map { (cat, list) ->
                        CategorySummary(
                            category = cat,
                            amount = list.sumOf { it.amount },
                            transactionCount = list.size
                        )
                    }
                    .sortedByDescending { it.amount }
                    .map { cs ->
                        cs.copy(percentage = if (expense > 0) (cs.amount / expense).toFloat() else 0f)
                    }

                val incomeByCategory = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .groupBy { it.category }
                    .map { (cat, list) ->
                        CategorySummary(
                            category = cat,
                            amount = list.sumOf { it.amount },
                            transactionCount = list.size
                        )
                    }
                    .sortedByDescending { it.amount }

                _laporanState.update {
                    it.copy(
                        monthlyIncome = income,
                        monthlyExpense = expense,
                        categoryExpenses = expenseByCategory,
                        categoryIncomes = incomeByCategory,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    fun setLaporanMonth(month: Int, year: Int) {
        _laporanState.update { it.copy(selectedMonth = month, selectedYear = year, isLoading = true) }
        loadLaporanData()
    }

    // ── CRUD Transaksi ───────────────────────────────────────────────────────
    fun tambahTransaksi(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaksi(old: Transaction, new: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(old, new)
        }
    }

    fun hapusTransaksi(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun loadTransactionById(id: Long) {
        viewModelScope.launch {
            val t = repository.getAllTransactions().first().find { it.id == id }
            _selectedTransaction.value = t
        }
    }

    // ── CRUD Akun ─────────────────────────────────────────────────────────────────
    fun tambahAkun(account: Account) {
        viewModelScope.launch { repository.insertAccount(account) }
    }

    fun updateAkun(account: Account) {
        viewModelScope.launch { repository.updateAccount(account) }
    }

    fun hapusAkun(account: Account) {
        viewModelScope.launch { repository.deleteAccount(account) }
    }

    fun setDefaultAkun(accountId: Long) {
        viewModelScope.launch { repository.setDefaultAccount(accountId) }
    }

    fun transfer(fromId: Long, toId: Long, amount: Double) {
        viewModelScope.launch { repository.transferBetweenAccounts(fromId, toId, amount) }
    }

    // ── CRUD Budget ─────────────────────────────────────────────────────────────
    fun tambahBudget(budget: Budget) {
        viewModelScope.launch { repository.insertBudget(budget) }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch { repository.updateBudget(budget) }
    }

    fun hapusBudget(budget: Budget) {
        viewModelScope.launch { repository.deleteBudget(budget) }
    }
}
