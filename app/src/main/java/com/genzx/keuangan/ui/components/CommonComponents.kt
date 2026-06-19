package com.genzx.keuangan.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.util.FormatUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// ── Transaction Item ──────────────────────────────────────────────────────
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: (() -> Unit)? = null
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val categoryIcon = CATEGORY_ICONS[transaction.category] ?: if (isIncome) "💰" else "💸"
    val categoryColorIndex = (EXPENSE_CATEGORIES.indexOf(transaction.category)
        .let { if (it == -1) INCOME_CATEGORIES.indexOf(transaction.category) else it }
        .coerceAtLeast(0)) % CategoryColors.size
    val categoryColor = CategoryColors[categoryColorIndex]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(SurfaceLight)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(categoryIcon, fontSize = 22.sp)
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
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    transaction.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor
                )
                Text("•", style = MaterialTheme.typography.labelSmall, color = TextHint)
                Text(
                    FormatUtil.formatDateShort(transaction.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextHint
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${if (isIncome) "+" else "-"}${FormatUtil.formatCurrencyShort(transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isIncome) IncomeGreenDark else ExpenseRedDark,
                fontWeight = FontWeight.Bold
            )
            if (transaction.note.isNotEmpty()) {
                Text(
                    transaction.note,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextHint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 80.dp)
                )
            }
        }
    }
}

// ── Balance Card ─────────────────────────────────────────────────────────
@Composable
fun BalanceSummaryCard(
    income: Double,
    expense: Double,
    isHidden: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(OceanDeep, OceanLight)
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BalanceChip("Pemasukan", income, true, isHidden)
        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.3f)))
        BalanceChip("Pengeluaran", expense, false, isHidden)
    }
}

@Composable
private fun BalanceChip(label: String, amount: Double, isIncome: Boolean, isHidden: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(if (isIncome) IncomeGreen.copy(0.25f) else ExpenseRed.copy(0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                null, tint = if (isIncome) IncomeGreen else ExpenseRed,
                modifier = Modifier.size(16.dp)
            )
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.7f))
            Text(
                if (isHidden) "••••" else FormatUtil.formatCurrencyShort(amount),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Budget Progress Card ───────────────────────────────────────────────
@Composable
fun BudgetProgressCard(budget: Budget) {
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${CATEGORY_ICONS[budget.category] ?: "📦"} ${budget.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = progressColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        "${(budget.progressPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { budget.progressPercent },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = progressColor,
                trackColor = SurfaceVariantLight
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(FormatUtil.formatCurrencyShort(budget.spentAmount), style = MaterialTheme.typography.labelSmall, color = progressColor)
                Text(
                    if (budget.isOverBudget) "Over ${FormatUtil.formatCurrencyShort(-budget.remainingAmount)}"
                    else "Sisa ${FormatUtil.formatCurrencyShort(budget.remainingAmount)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (budget.isOverBudget) ExpenseRed else TextHint
                )
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                Text(actionLabel, style = MaterialTheme.typography.labelMedium, color = OceanLight)
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────
@Composable
fun EmptyState(title: String, subtitle: String, emoji: String = "📦") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextHint,
            textAlign = TextAlign.Center
        )
    }
}

// ── GenZx Top Bar ────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenZxTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
            )
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

// ── Stat Chip ───────────────────────────────────────────────────────────────────
@Composable
fun StatChip(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Month Picker Dialog ────────────────────────────────────────────────────
@Composable
fun MonthYearPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
) {
    var selectedMonth by remember { mutableIntStateOf(currentMonth) }
    var selectedYear by remember { mutableIntStateOf(currentYear) }
    val months = FormatUtil.getMonthNames()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "📅 Pilih Bulan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Year selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.Default.ChevronLeft, null, tint = OceanLight)
                    }
                    Text(
                        selectedYear.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = {
                        if (selectedYear < LocalDate.now().year) selectedYear++
                    }) {
                        Icon(Icons.Default.ChevronRight, null, tint = OceanLight)
                    }
                }

                // Month grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (0..2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (1..4).forEach { col ->
                                val monthIdx = row * 4 + col
                                if (monthIdx <= 12) {
                                    val isSelected = monthIdx == selectedMonth
                                    Surface(
                                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedMonth = monthIdx },
                                        color = if (isSelected) OceanLight else SurfaceVariantLight,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            months[monthIdx - 1].take(3),
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            textAlign = TextAlign.Center,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMonth, selectedYear) },
                colors = ButtonDefaults.buttonColors(containerColor = OceanLight),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Pilih") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextSecondary) }
        }
    )
}
