package com.genzx.keuangan.ui.screen.laporan

import androidx.compose.foundation.*
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
import com.genzx.keuangan.domain.model.CategorySummary
import com.genzx.keuangan.domain.model.CATEGORY_ICONS
import com.genzx.keuangan.ui.components.EmptyState
import com.genzx.keuangan.ui.components.MonthYearPickerDialog
import com.genzx.keuangan.ui.components.SectionHeader
import com.genzx.keuangan.ui.screen.jurnal.MonthPickerDialog
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    viewModel: MainViewModel,
    onNavigateToWrapped: (Int, Int) -> Unit
) {
    val state by viewModel.laporanState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "📊 Laporan",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { showMonthPicker = true }) {
                            Icon(Icons.Default.DateRange, null, tint = Color.White)
                        }
                        IconButton(onClick = { onNavigateToWrapped(state.selectedMonth, state.selectedYear) }) {
                            Icon(Icons.Default.Stars, null, tint = AmberAccent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Period summary card
            item {
                PeriodSummaryCard(
                    month = state.selectedMonth,
                    year = state.selectedYear,
                    income = state.monthlyIncome,
                    expense = state.monthlyExpense,
                    onChangePeriod = { showMonthPicker = true }
                )
            }

            // Category tabs
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceLight)
                        .padding(4.dp)
                ) {
                    Row {
                        TabButton(
                            label = "📉 Pengeluaran",
                            selected = selectedTab == 0,
                            modifier = Modifier.weight(1f)
                        ) { selectedTab = 0 }
                        TabButton(
                            label = "📈 Pemasukan",
                            selected = selectedTab == 1,
                            modifier = Modifier.weight(1f)
                        ) { selectedTab = 1 }
                    }
                }
            }

            // Category breakdown
            val categories = if (selectedTab == 0) state.categoryExpenses else state.categoryIncomes
            if (categories.isEmpty()) {
                item {
                    EmptyState(
                        title = "Belum ada data",
                        subtitle = "Tambah transaksi untuk melihat laporan",
                        emoji = "📊"
                    )
                }
            } else {
                item {
                    SectionHeader(
                        title = if (selectedTab == 0) "Pengeluaran per Kategori" else "Pemasukan per Kategori"
                    )
                }
                items(categories) { category ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        CategoryItem(
                            summary = category,
                            total = if (selectedTab == 0) state.monthlyExpense else state.monthlyIncome,
                            isExpense = selectedTab == 0
                        )
                    }
                }
            }

            // Top spending insight
            if (state.categoryExpenses.isNotEmpty()) {
                item {
                    TopSpendingInsight(state.categoryExpenses)
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
                viewModel.setLaporanMonth(m, y)
                showMonthPicker = false
            }
        )
    }
}

@Composable
fun PeriodSummaryCard(
    month: Int,
    year: Int,
    income: Double,
    expense: Double,
    onChangePeriod: () -> Unit
) {
    val net = income - expense
    val savingRate = if (income > 0) (net / income * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(OceanDeep, OceanBright))
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            FormatUtil.formatMonthYear(month, year),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tingkat tabungan: $savingRate%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(0.7f)
                        )
                    }
                    IconButton(
                        onClick = onChangePeriod,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color.White.copy(0.15f))
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SummaryPill("+${FormatUtil.formatCurrencyShort(income)}", "Pemasukan", IncomeGreen)
                    SummaryPill("-${FormatUtil.formatCurrencyShort(expense)}", "Pengeluaran", ExpenseRed)
                    SummaryPill(
                        if (net >= 0) "+${FormatUtil.formatCurrencyShort(net)}" else "-${FormatUtil.formatCurrencyShort(-net)}",
                        "Net",
                        if (net >= 0) AmberAccent else RoseAccent
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryPill(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = color.copy(0.2f)
        ) {
            Text(
                value,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.6f))
    }
}

@Composable
fun TabButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) OceanLight else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CategoryItem(summary: CategorySummary, total: Double, isExpense: Boolean) {
    val categoryColor = CategoryColors[(EXPENSE_CATEGORIES.indexOf(summary.category)
        .let { if (it == -1) 0 else it }) % CategoryColors.size]
    val icon = CATEGORY_ICONS[summary.category] ?: if (isExpense) "💸" else "💰"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(categoryColor.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 18.sp)
                    }
                    Column {
                        Text(
                            summary.category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            "${summary.transactionCount} transaksi",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        FormatUtil.formatCurrencyShort(summary.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isExpense) ExpenseRedDark else IncomeGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${(summary.percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextHint
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { summary.percentage },
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(4.dp)),
                color = categoryColor,
                trackColor = SurfaceVariantLight
            )
        }
    }
}

@Composable
fun TopSpendingInsight(categories: List<CategorySummary>) {
    val top = categories.firstOrNull() ?: return
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VioletContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🤔", fontSize = 28.sp)
            Column {
                Text(
                    "Pengeluaran Terbesar",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnVioletContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${CATEGORY_ICONS[top.category] ?: "📦"} ${top.category} menghabiskan ${FormatUtil.formatCurrencyShort(top.amount)} (${(top.percentage * 100).toInt()}% dari pengeluaran)",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnVioletContainer
                )
            }
        }
    }
}
