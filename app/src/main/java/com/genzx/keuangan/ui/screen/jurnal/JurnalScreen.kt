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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.Transaction
import com.genzx.keuangan.domain.model.TransactionType
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

    // Group transactions by date
    val grouped = state.filteredTransactions
        .groupBy { it.date.take(10) }
        .toSortedMap(compareByDescending { it })

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Jurnal 📝",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMonthPicker = true }) {
                            Icon(Icons.Default.DateRange, "Bulan", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Teal)
                )
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
                    modifier = Modifier.fillMaxWidth().background(Teal).padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        focusedBorderColor = Color.Transparent,
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
            // Month header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FormatUtil.formatMonthYear(state.selectedMonth, state.selectedYear),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.filteredTransactions.size} transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Summary
            item {
                val income = state.filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expense = state.filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JurnalSummaryChip(
                        label = "Masuk",
                        amount = income,
                        color = IncomeGreenLight,
                        textColor = IncomeGreen,
                        modifier = Modifier.weight(1f)
                    )
                    JurnalSummaryChip(
                        label = "Keluar",
                        amount = expense,
                        color = ExpenseRedLight,
                        textColor = ExpenseRed,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Filter tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        FilterChip(
                            selected = state.selectedFilter == filter,
                            onClick = { viewModel.setJurnalFilter(filter) },
                            label = { Text(filter, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Teal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (state.filteredTransactions.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "📖",
                        title = "Tidak ada transaksi",
                        subtitle = if (state.searchQuery.isNotEmpty()) "Coba kata kunci lain" else "Belum ada transaksi di bulan ini"
                    )
                }
            } else {
                grouped.forEach { (dateStr, transactions) ->
                    // Date header
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundLight)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            val date = try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { null }
                            val today = LocalDate.now()
                            val label = when (date) {
                                today -> "Hari ini"
                                today.minusDays(1) -> "Kemarin"
                                else -> FormatUtil.formatDateFull(dateStr + "T00:00:00")
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = Teal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Transactions for this date
                    items(transactions) { transaction ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
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

    // Month Picker Dialog
    if (showMonthPicker) {
        MonthPickerDialog(
            currentMonth = state.selectedMonth,
            currentYear = state.selectedYear,
            onDismiss = { showMonthPicker = false },
            onConfirm = { month, year ->
                viewModel.setJurnalMonth(month, year)
                showMonthPicker = false
            }
        )
    }
}

@Composable
fun JurnalSummaryChip(
    label: String,
    amount: Double,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = textColor)
        Text(FormatUtil.formatCurrencyShort(amount), style = MaterialTheme.typography.titleSmall, color = textColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MonthPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedMonth by remember { mutableIntStateOf(currentMonth) }
    var selectedYear by remember { mutableIntStateOf(currentYear) }
    val months = FormatUtil.getMonthNames()
    val years = (2020..java.time.LocalDate.now().year + 1).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Bulan", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                // Year selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (selectedYear > 2020) selectedYear-- }) {
                        Icon(Icons.Default.ChevronLeft, null)
                    }
                    Text("$selectedYear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Month grid
                val chunked = months.chunked(3)
                chunked.forEachIndexed { rowIdx, rowMonths ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        rowMonths.forEachIndexed { colIdx, month ->
                            val monthIdx = rowIdx * 3 + colIdx + 1
                            val isSelected = monthIdx == selectedMonth
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Teal else SurfaceVariant)
                                    .clickable { selectedMonth = monthIdx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = month.take(3),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMonth, selectedYear) },
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) { Text("Pilih") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
