package com.genzx.keuangan.ui.screen.jurnal

import androidx.compose.foundation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.Transaction
import com.genzx.keuangan.domain.model.TransactionType
import com.genzx.keuangan.domain.model.CATEGORY_ICONS
import com.genzx.keuangan.ui.components.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun JurnalScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToTambah: (String) -> Unit
) {
    val state by viewModel.jurnalState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    val filters = listOf("Semua", "Pemasukan", "Pengeluaran")

    val grouped = state.filteredTransactions
        .groupBy { it.date.take(10) }
        .toSortedMap(compareByDescending { it })

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                "Jurnal 📝",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { showMonthPicker = true }) {
                                Icon(Icons.Default.DateRange, null, tint = Color.White)
                            }
                            IconButton(onClick = { onNavigateToTambah("EXPENSE") }) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceLight)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val selected = state.selectedFilter == filter
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setJurnalFilter(filter) },
                            label = { Text(filter, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OceanLight,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceVariantLight
                            ),
                            border = null
                        )
                    }
                }

                // Search bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.searchJurnal(it) },
                    placeholder = { Text("Cari transaksi...", color = TextHint) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchJurnal("") }) {
                                Icon(Icons.Default.Close, null, tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceLight)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight,
                        focusedBorderColor = OceanLight,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Month header + summary
            item {
                MonthSummaryCard(state)
            }

            if (state.filteredTransactions.isEmpty()) {
                item {
                    EmptyState(
                        title = "Tidak ada transaksi",
                        subtitle = if (state.searchQuery.isNotEmpty()) "Coba kata kunci lain" else "Belum ada transaksi di bulan ini",
                        emoji = "📝"
                    )
                }
            } else {
                grouped.forEach { (dateStr, transactions) ->
                    stickyHeader {
                        DateGroupHeader(dateStr)
                    }
                    items(transactions) { transaction ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)) {
                            TransactionItem(
                                transaction = transaction,
                                onClick = { onNavigateToEdit(transaction.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMonthPicker) {
        MonthYearPickerDialog(
            currentMonth = state.selectedMonth,
            currentYear = state.selectedYear,
            onDismiss = { showMonthPicker = false },
            onConfirm = { m, y ->
                viewModel.setJurnalMonth(m, y)
                showMonthPicker = false
            }
        )
    }
}

@Composable
fun MonthSummaryCard(state: com.genzx.keuangan.ui.viewmodel.JurnalUiState) {
    val income = state.transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expense = state.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                FormatUtil.formatMonthYear(state.selectedMonth, state.selectedYear),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "${state.transactions.size} transaksi",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = IncomeGreen.copy(alpha = 0.1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("📈 Pemasukan", style = MaterialTheme.typography.labelSmall, color = IncomeGreenDark)
                        Text(FormatUtil.formatCurrencyShort(income), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = IncomeGreenDark)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = ExpenseRed.copy(alpha = 0.1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("📉 Pengeluaran", style = MaterialTheme.typography.labelSmall, color = ExpenseRedDark)
                        Text(FormatUtil.formatCurrencyShort(expense), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ExpenseRedDark)
                    }
                }
            }
        }
    }
}

@Composable
fun DateGroupHeader(dateStr: String) {
    val date = try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { null }
    val today = LocalDate.now()
    val label = when (date) {
        today -> "🗓 Hari ini"
        today.minusDays(1) -> "🗓 Kemarin"
        else -> "🗓 ${date?.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM", java.util.Locale("id"))) ?: dateStr}"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Keep for NavGraph compat
@Composable
fun MonthPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
) {
    MonthYearPickerDialog(currentMonth, currentYear, onDismiss, onConfirm)
}
