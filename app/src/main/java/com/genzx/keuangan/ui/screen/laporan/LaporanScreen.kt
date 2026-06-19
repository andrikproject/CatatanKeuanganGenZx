package com.genzx.keuangan.ui.screen.laporan

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.CategorySummary
import com.genzx.keuangan.ui.components.EmptyState
import com.genzx.keuangan.ui.components.SectionHeader
import com.genzx.keuangan.ui.screen.jurnal.MonthPickerDialog
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.domain.model.CATEGORY_ICONS
import com.genzx.keuangan.util.FormatUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    viewModel: MainViewModel,
    onNavigateToWrapped: (Int, Int) -> Unit
) {
    val state by viewModel.laporanState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Pengeluaran, 1=Pemasukan

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Laporan", style = MaterialTheme.typography.headlineMedium, color = Color.White) },
                actions = {
                    IconButton(onClick = { showMonthPicker = true }) {
                        Icon(Icons.Default.CalendarMonth, "Bulan", tint = Color.White)
                    }
                    IconButton(onClick = { onNavigateToWrapped(state.selectedMonth, state.selectedYear) }) {
                        Icon(Icons.Default.AutoAwesome, "Wrapped", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Teal)
            )
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
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FormatUtil.formatMonthYear(state.selectedMonth, state.selectedYear),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showMonthPicker = true }) {
                        Icon(Icons.Default.ExpandMore, null, tint = Teal)
                    }
                }
            }

            // Overview cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OverviewCard(
                        label = "Pemasukan",
                        amount = state.monthlyIncome,
                        color = IncomeGreenLight,
                        textColor = IncomeGreen,
                        emoji = "💰",
                        modifier = Modifier.weight(1f)
                    )
                    OverviewCard(
                        label = "Pengeluaran",
                        amount = state.monthlyExpense,
                        color = ExpenseRedLight,
                        textColor = ExpenseRed,
                        emoji = "📉",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val net = state.monthlyIncome - state.monthlyExpense
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (net >= 0) IncomeGreenLight else ExpenseRedLight
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (net >= 0) "💪 Surplus bulan ini" else "⚠️ Defisit bulan ini",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (net >= 0) IncomeGreen else ExpenseRed
                        )
                        Text(
                            text = (if (net >= 0) "+" else "") + FormatUtil.formatCurrencyShort(net),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (net >= 0) IncomeGreen else ExpenseRed
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tab: Pengeluaran / Pemasukan
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceLight,
                    contentColor = Teal,
                    modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("📉 Pengeluaran") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("📈 Pemasukan") }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val categories = if (selectedTab == 0) state.categoryExpenses else state.categoryIncomes
            val total = if (selectedTab == 0) state.monthlyExpense else state.monthlyIncome

            if (categories.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "📊",
                        title = "Belum ada data",
                        subtitle = "Tambah transaksi untuk melihat laporan"
                    )
                }
            } else {
                items(categories) { summary ->
                    CategoryBar(
                        summary = summary,
                        totalAmount = total
                    )
                }
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            currentMonth = state.selectedMonth,
            currentYear = state.selectedYear,
            onDismiss = { showMonthPicker = false },
            onConfirm = { month, year ->
                viewModel.setLaporanMonth(month, year)
                showMonthPicker = false
            }
        )
    }
}

@Composable
fun OverviewCard(
    label: String,
    amount: Double,
    color: Color,
    textColor: Color,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = textColor)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = FormatUtil.formatCurrencyShort(amount),
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryBar(
    summary: CategorySummary,
    totalAmount: Double
) {
    val emoji = CATEGORY_ICONS[summary.category] ?: "📦"
    val colorIdx = CATEGORY_ICONS.keys.indexOf(summary.category).let { if (it < 0) 0 else it % CategoryColors.size }
    val barColor = CategoryColors[colorIdx]

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = summary.category,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${summary.transactionCount} transaksi",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FormatUtil.formatCurrencyShort(summary.amount),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${(summary.percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = barColor
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = summary.percentage,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = barColor,
            trackColor = SurfaceVariant
        )
    }
}
