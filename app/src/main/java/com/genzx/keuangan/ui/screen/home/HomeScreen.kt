package com.genzx.keuangan.ui.screen.home

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.*
import com.genzx.keuangan.ui.components.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil
import java.time.LocalDate

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

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                HomeHeader(
                    state = state,
                    onToggleBalance = { viewModel.toggleBalanceVisibility() },
                    onNavigateToAkun = onNavigateToAkun
                )
            }

            // Quick Actions
            item {
                QuickActions(
                    onPemasukan = { onNavigateToTambah("INCOME") },
                    onPengeluaran = { onNavigateToTambah("EXPENSE") },
                    onJurnal = onNavigateToJurnal,
                    onAkun = onNavigateToAkun
                )
            }

            // AI Insight
            item {
                val insight = FormatUtil.generateAiInsight(
                    state.monthlyIncome,
                    state.monthlyExpense,
                    null
                )
                AiInsightCard(insight = insight)
            }

            // Akun Section
            if (state.accounts.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Akun",
                        actionText = "Lihat Semua",
                        onAction = onNavigateToAkun
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.accounts) { account ->
                            AccountCard(
                                account = account,
                                onClick = onNavigateToAkun
                            )
                        }
                    }
                }
            }

            // Budget Section
            if (state.budgets.isNotEmpty()) {
                item {
                    SectionHeader(title = "Budget Bulan Ini")
                }
                items(state.budgets.take(3)) { budget ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        BudgetProgressCard(budget = budget)
                    }
                }
            }

            // Transaksi Terbaru
            item {
                SectionHeader(
                    title = "Transaksi Terbaru",
                    actionText = "Lihat Semua",
                    onAction = onNavigateToJurnal
                )
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "💸",
                        title = "Belum ada transaksi",
                        subtitle = "Yuk catat pemasukan atau pengeluaran pertamamu!"
                    )
                }
            } else {
                items(state.recentTransactions) { transaction ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        TransactionItem(
                            transaction = transaction,
                            onClick = onNavigateToJurnal
                        )
                    }
                }
            }

            // Wrapped Banner
            item {
                WrappedBanner(
                    month = now.monthValue,
                    year = now.year,
                    onClick = { onNavigateToWrapped(now.monthValue, now.year) }
                )
            }
        }
    }
}

@Composable
fun HomeHeader(
    state: com.genzx.keuangan.ui.viewmodel.HomeUiState,
    onToggleBalance: () -> Unit,
    onNavigateToAkun: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TealDark, Teal)
                )
            )
            .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Column {
            // Greeting row
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
                        text = "GenZx 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onNavigateToAkun() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👤", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Balance
            Text(
                text = "Total Saldo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (state.isBalanceHidden) "••••••••"
                           else FormatUtil.formatCurrency(state.totalBalance),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onToggleBalance) {
                    Icon(
                        imageVector = if (state.isBalanceHidden)
                            Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle balance",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Income/Expense summary
            SummaryCard(
                income = state.monthlyIncome,
                expense = state.monthlyExpense,
                isHidden = state.isBalanceHidden
            )
        }
    }
}

@Composable
fun QuickActions(
    onPemasukan: () -> Unit,
    onPengeluaran: () -> Unit,
    onJurnal: () -> Unit,
    onAkun: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionItem(emoji = "➕", label = "Pemasukan", color = IncomeGreenLight, onClick = onPemasukan)
        QuickActionItem(emoji = "➖", label = "Pengeluaran", color = ExpenseRedLight, onClick = onPengeluaran)
        QuickActionItem(emoji = "📝", label = "Jurnal", color = LilacContainer, onClick = onJurnal)
        QuickActionItem(emoji = "💳", label = "Akun", color = MintContainer, onClick = onAkun)
    }
}

@Composable
fun QuickActionItem(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
fun AiInsightCard(insight: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TealContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = "🤖", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AI Insight",
                    style = MaterialTheme.typography.labelMedium,
                    color = TealDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnTealContainer
                )
            }
        }
    }
}

@Composable
fun WrappedBanner(
    month: Int,
    year: Int,
    onClick: () -> Unit
) {
    val monthName = FormatUtil.getMonthNames().getOrNull(month - 1) ?: ""
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rekap $monthName 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Buka & Bagikan Story",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Text(text = "🌊", fontSize = 36.sp)
            }
        }
    }
}

fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 11 -> "Selamat pagi"
        hour < 15 -> "Selamat siang"
        hour < 18 -> "Selamat sore"
        else -> "Selamat malam"
    }
}
