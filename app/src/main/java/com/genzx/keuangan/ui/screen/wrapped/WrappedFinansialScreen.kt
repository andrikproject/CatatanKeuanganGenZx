package com.genzx.keuangan.ui.screen.wrapped

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrappedFinansialScreen(
    viewModel: MainViewModel,
    month: Int,
    year: Int,
    onNavigateBack: () -> Unit
) {
    val allTransactions by viewModel.repository.getAllTransactions().collectAsState(emptyList())
    val monthTransactions = allTransactions.filter {
        it.date.startsWith("%04d-%02d".format(year, month))
    }

    val totalIncome = monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val net = totalIncome - totalExpense

    val topExpenseCategory = monthTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .maxByOrNull { it.value.sumOf { t -> t.amount } }?.key

    val topIncomeCategory = monthTransactions
        .filter { it.type == TransactionType.INCOME }
        .groupBy { it.category }
        .maxByOrNull { it.value.sumOf { t -> t.amount } }?.key

    val persona = when {
        net > totalIncome * 0.5 -> "Sang Penabung Sejati 🌟"
        net > 0 -> "Sang Navigator Cicilan 🧭"
        totalExpense > totalIncome * 1.2 -> "Sang Petualang Royal 😅"
        else -> "Sang Pejuang Finansial 💪"
    }

    val personaDesc = when {
        net > totalIncome * 0.5 -> "Wow, kamu hemat banget bulan ini! Investasi yuk biar uang kerja sendiri."
        net > 0 -> "Kamu pintar atur keuangan, tapi masih ada ruang buat lebih hemat lagi!"
        totalExpense > totalIncome * 1.2 -> "Bulan ini agak royal ya... gapapa, hidup sekali! Tapi bulan depan kita lebih bijak ya 😉"
        else -> "Tetap semangat! Setiap langkah kecil menuju finansial sehat itu berarti."
    }

    val monthName = FormatUtil.getMonthNames().getOrNull(month - 1) ?: ""
    val insight = FormatUtil.generateAiInsight(totalIncome, totalExpense, topExpenseCategory)

    var currentPage by remember { mutableIntStateOf(0) }
    val pages = 4

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekap $monthName $year", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Tutup", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when (currentPage) {
                        0 -> Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))
                        1 -> Brush.verticalGradient(listOf(Color(0xFF004D5A), Color(0xFF006B7A)))
                        2 -> Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF7B1FA2)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)))
                    }
                )
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                },
                label = "wrapped_page"
            ) { page ->
                when (page) {
                    0 -> WrappedPage1(monthName, year, totalIncome, totalExpense, net, Modifier.padding(padding))
                    1 -> WrappedPage2(topExpenseCategory, topIncomeCategory, monthTransactions.size, Modifier.padding(padding))
                    2 -> WrappedPage3(persona, personaDesc, Modifier.padding(padding))
                    else -> WrappedPage4(insight, net, Modifier.padding(padding))
                }
            }

            // Page indicators
            Row(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(pages) { i ->
                    Box(
                        modifier = Modifier
                            .width(if (i == currentPage) 24.dp else 8.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (i == currentPage) Color.White else Color.White.copy(alpha = 0.4f))
                            .animateContentSize()
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentPage > 0) {
                    OutlinedButton(
                        onClick = { currentPage-- },
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.ChevronLeft, null)
                        Text("Sebelumnya")
                    }
                } else Spacer(modifier = Modifier.width(1.dp))

                if (currentPage < pages - 1) {
                    Button(
                        onClick = { currentPage++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Selanjutnya", color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF1A1A2E))
                    }
                } else {
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("🎉 Selesai", color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WrappedPage1(monthName: String, year: Int, income: Double, expense: Double, net: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌊", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text("Rekap Keuangan Kamu", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(40.dp))
        WrappedStatCard(label = "💰 Total Pemasukan", value = FormatUtil.formatCurrencyShort(income), color = Color(0xFF00E676))
        Spacer(modifier = Modifier.height(12.dp))
        WrappedStatCard(label = "📉 Total Pengeluaran", value = FormatUtil.formatCurrencyShort(expense), color = Color(0xFFFF5252))
        Spacer(modifier = Modifier.height(12.dp))
        WrappedStatCard(
            label = if (net >= 0) "💪 Net Surplus" else "⚠️ Net Defisit",
            value = (if (net >= 0) "+" else "") + FormatUtil.formatCurrencyShort(net),
            color = if (net >= 0) Color(0xFF00E676) else Color(0xFFFF5252)
        )
    }
}

@Composable
fun WrappedPage2(topExpense: String?, topIncome: String?, totalCount: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔍", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sorotan Bulan Ini", style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(40.dp))

        if (topExpense != null) {
            WrappedHighlightCard(
                emoji = CATEGORY_ICONS[topExpense] ?: "📦",
                title = "Pengeluaran Terbesar",
                value = topExpense,
                color = Color(0xFFFF5252)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (topIncome != null) {
            WrappedHighlightCard(
                emoji = CATEGORY_ICONS[topIncome] ?: "💰",
                title = "Pemasukan Utama",
                value = topIncome,
                color = Color(0xFF00E676)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        WrappedHighlightCard(
            emoji = "📝",
            title = "Total Transaksi Dicatat",
            value = "$totalCount transaksi",
            color = Color(0xFF40C4FF)
        )
    }
}

@Composable
fun WrappedPage3(persona: String, desc: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏆", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Persona Finansialmu", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = persona,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Text(
                text = desc,
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WrappedPage4(insight: String, net: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (net >= 0) "🎉" else "💪", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Pesan dari AI", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Text(
                text = insight,
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Bagikan rekap ini ke teman-temanmu! 🌟",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WrappedStatCard(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WrappedHighlightCard(emoji: String, title: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
