package com.genzx.keuangan.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.ui.viewmodel.HomeUiState
import com.genzx.keuangan.util.FormatUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToTambah: (String) -> Unit,
    onNavigateToJurnal: () -> Unit,
    onNavigateToAkun: () -> Unit,
    onNavigateToWrapped: (Int, Int) -> Unit
) {
    val state by viewModel.homeState.collectAsState()
    val now = LocalDate.now()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header dengan gradient ocean
            item { HomeHeader(state, viewModel, now, onNavigateToWrapped) }

            // Quick action buttons
            item { QuickActions(onNavigateToTambah, onNavigateToJurnal, onNavigateToAkun) }

            // Summary cards (income/expense/saving)
            item { SummaryCards(state) }

            // Akun overview
            if (state.accounts.isNotEmpty()) {
                item { AkunSection(state.accounts, onNavigateToAkun) }
            }

            // Budget overview
            if (state.budgets.isNotEmpty()) {
                item { BudgetSection(state.budgets) }
            }

            // Recent transactions
            item { RecentTransactionHeader(onNavigateToJurnal) }

            if (state.recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionState(onNavigateToTambah)
                }
            } else {
                items(state.recentTransactions) { transaction ->
                    ModernTransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    state: HomeUiState,
    viewModel: MainViewModel,
    now: LocalDate,
    onNavigateToWrapped: (Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Gradient background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.linearGradient(
                colors = listOf(
                    GradientStart,
                    GradientMid,
                    GradientEnd
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
            drawRect(brush = gradient)

            // Decorative circles
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.9f, -size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.4f,
                center = Offset(-size.width * 0.1f, size.height * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top row: greeting + notification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getGreeting(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Catatan Keuangan ✨",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onNavigateToWrapped(now.monthValue, now.year) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.Stars, null, tint = AmberAccent, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Balance card
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Total Saldo",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (state.isBalanceHidden) "Rp ••••••" else FormatUtil.formatCurrency(state.totalBalance),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.toggleBalanceVisibility() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = now.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id"))),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Income / Expense row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IncomeExpenseChip(
                    label = "Pemasukan",
                    amount = state.monthlyIncome,
                    isIncome = true,
                    isHidden = state.isBalanceHidden
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                )
                IncomeExpenseChip(
                    label = "Pengeluaran",
                    amount = state.monthlyExpense,
                    isIncome = false,
                    isHidden = state.isBalanceHidden
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseChip(
    label: String,
    amount: Double,
    isIncome: Boolean,
    isHidden: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isIncome) IncomeGreen.copy(alpha = 0.2f) else ExpenseRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = if (isIncome) IncomeGreen else ExpenseRed,
                modifier = Modifier.size(16.dp)
            )
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
            Text(
                text = if (isHidden) "••••" else FormatUtil.formatCurrencyShort(amount),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun QuickActions(
    onNavigateToTambah: (String) -> Unit,
    onNavigateToJurnal: () -> Unit,
    onNavigateToAkun: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionItem("Pengeluaran", Icons.Default.Remove, ExpenseRed) {
            onNavigateToTambah("EXPENSE")
        }
        QuickActionItem("Pemasukan", Icons.Default.Add, IncomeGreen) {
            onNavigateToTambah("INCOME")
        }
        QuickActionItem("Jurnal", Icons.Default.List, OceanLight) {
            onNavigateToJurnal()
        }
        QuickActionItem("Akun", Icons.Default.AccountBalance, VioletAccent) {
            onNavigateToAkun()
        }
    }
}

@Composable
fun QuickActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SummaryCards(state: HomeUiState) {
    val savings = state.monthlyIncome - state.monthlyExpense
    val savingsRate = if (state.monthlyIncome > 0) (savings / state.monthlyIncome * 100).toInt() else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Net saving card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (savings >= 0) IncomeGreenDark else ExpenseRedDark
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp, null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tabungan", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    FormatUtil.formatCurrencyShort(savings),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${savingsRate}% dari pemasukan",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Spending rate card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VioletAccent),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PieChart, null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rasio Belanja", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                val spendRate = if (state.monthlyIncome > 0) (state.monthlyExpense / state.monthlyIncome * 100).toInt() else 0
                Text(
                    "${spendRate}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "dari total pemasukan",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AkunSection(accounts: List<Account>, onNavigateToAkun: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Akun Saya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            TextButton(onClick = onNavigateToAkun) {
                Text("Lihat Semua", style = MaterialTheme.typography.labelMedium, color = OceanLight)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        accounts.take(3).forEachIndexed { idx, account ->
            val gradientColors = when (idx % 4) {
                0 -> listOf(CardBlue1, CardBlue2)
                1 -> listOf(CardGreen1, CardGreen2)
                2 -> listOf(CardPurple1, CardPurple2)
                else -> listOf(CardRose1, CardRose2)
            }
            AkunCard(account, gradientColors)
            if (idx < minOf(accounts.size - 1, 2)) Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AkunCard(account: Account, gradientColors: List<Color>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(gradientColors)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (account.type) {
                                AccountType.CASH -> "💵"
                                AccountType.BANK -> "🏦"
                                AccountType.SAVINGS -> "💎"
                                AccountType.EWALLET -> "📱"
                            },
                            fontSize = 18.sp
                        )
                    }
                    Column {
                        Text(account.name, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(
                            account.type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        FormatUtil.formatCurrencyShort(account.balance),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (account.isDefault) {
                        Text("Utama", style = MaterialTheme.typography.labelSmall, color = AmberAccent)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetSection(budgets: List<Budget>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Text("Budget Bulan Ini", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        budgets.take(3).forEach { budget ->
            BudgetProgressItem(budget)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BudgetProgressItem(budget: Budget) {
    val progressColor = when {
        budget.isOverBudget -> ExpenseRed
        budget.progressPercent > 0.8f -> WarningAmber
        else -> IncomeGreen
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "${CATEGORY_ICONS[budget.category] ?: "📦"} ${budget.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    "${(budget.progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = progressColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { budget.progressPercent },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = progressColor,
                trackColor = SurfaceVariantLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(FormatUtil.formatCurrencyShort(budget.spentAmount), style = MaterialTheme.typography.labelSmall, color = progressColor)
                Text(FormatUtil.formatCurrencyShort(budget.limitAmount), style = MaterialTheme.typography.labelSmall, color = TextHint)
            }
        }
    }
}

@Composable
fun RecentTransactionHeader(onNavigateToJurnal: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Transaksi Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        TextButton(onClick = onNavigateToJurnal) {
            Text("Lihat Semua", style = MaterialTheme.typography.labelMedium, color = OceanLight)
        }
    }
}

@Composable
fun ModernTransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == TransactionType.INCOME
    val categoryIcon = CATEGORY_ICONS[transaction.category] ?: if (isIncome) "💰" else "💸"
    val categoryColorIndex = EXPENSE_CATEGORIES.indexOf(transaction.category)
        .let { if (it == -1) INCOME_CATEGORIES.indexOf(transaction.category) else it }
        .coerceAtLeast(0) % CategoryColors.size
    val categoryColor = CategoryColors[categoryColorIndex]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceLight)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(categoryIcon, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                transaction.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                transaction.category,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (isIncome) "+" else "-"}${FormatUtil.formatCurrencyShort(transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isIncome) IncomeGreenDark else ExpenseRedDark,
                fontWeight = FontWeight.Bold
            )
            Text(
                FormatUtil.formatDateShort(transaction.date),
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
    }
}

@Composable
fun EmptyTransactionState(onNavigateToTambah: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💸", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Belum ada transaksi",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Mulai catat keuanganmu sekarang!",
            style = MaterialTheme.typography.bodySmall,
            color = TextHint,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigateToTambah("EXPENSE") },
            colors = ButtonDefaults.buttonColors(containerColor = OceanLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tambah Transaksi")
        }
    }
}

fun getGreeting(): String {
    return when (LocalDate.now().dayOfWeek.value) {
        1 -> "Semangat Senin! 🔥"
        5 -> "Happy Friday! 🎉"
        6, 7 -> "Selamat Weekend! 😊"
        else -> "Halo, GenZxer! 👋"
    }
}
