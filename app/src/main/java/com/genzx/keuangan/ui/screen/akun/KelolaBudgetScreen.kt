package com.genzx.keuangan.ui.screen.akun

// KelolaBudgetScreen.kt — placeholder, referenced in NavGraph
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun KelolaBudgetScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val now = LocalDate.now()
    // Collect budgets for current month
    val budgets by viewModel.repository.getBudgetsByMonth(now.monthValue, now.year)
        .collectAsState(emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GenZxTopBar(title = "Kelola Budget", onBack = onNavigateBack)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Teal) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Budget ${FormatUtil.formatMonthYear(now.monthValue, now.year)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Atur batas pengeluaran per kategori biar ga boncos 👀",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (budgets.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "🎯",
                        title = "Belum ada budget",
                        subtitle = "Tambah budget per kategori untuk kontrol pengeluaran"
                    )
                }
            } else {
                items(budgets) { budget ->
                    BudgetProgressCard(
                        budget = budget,
                        onClick = {}
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        TambahBudgetDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { budget ->
                viewModel.tambahBudget(budget)
                showAddDialog = false
            },
            month = now.monthValue,
            year = now.year
        )
    }
}

@Composable
fun TambahBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Budget) -> Unit,
    month: Int,
    year: Int
) {
    var selectedCategory by remember { mutableStateOf(EXPENSE_CATEGORIES.first()) }
    var limitAmount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎯 Tambah Budget", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category dropdown
                Text("Kategori", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = "${CATEGORY_ICONS[selectedCategory] ?: "📦"} $selectedCategory",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        EXPENSE_CATEGORIES.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${CATEGORY_ICONS[category] ?: "📦"} $category") },
                                onClick = { selectedCategory = category; expanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = limitAmount,
                    onValueChange = { limitAmount = it.filter { c -> c.isDigit() } },
                    label = { Text("Batas Budget") },
                    prefix = { Text("Rp ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitAmount.toDoubleOrNull()
                    if (limit != null && limit > 0) {
                        onConfirm(Budget(category = selectedCategory, limitAmount = limit, month = month, year = year))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) { Text("Tambah") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
