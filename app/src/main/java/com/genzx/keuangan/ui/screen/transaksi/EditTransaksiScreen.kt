package com.genzx.keuangan.ui.screen.transaksi

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.genzx.keuangan.domain.model.*
import com.genzx.keuangan.ui.components.GenZxTopBar
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransaksiScreen(
    viewModel: MainViewModel,
    transactionId: Long,
    onNavigateBack: () -> Unit
) {
    val selectedTransaction by viewModel.selectedTransaction.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.loadTransactionById(transactionId)
    }

    val transaction = selectedTransaction
    if (transaction == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Teal)
        }
        return
    }

    var selectedType by remember(transaction) { mutableStateOf(transaction.type) }
    var title by remember(transaction) { mutableStateOf(transaction.title) }
    var amount by remember(transaction) { mutableStateOf(transaction.amount.toLong().toString()) }
    var selectedCategory by remember(transaction) { mutableStateOf(transaction.category) }
    var selectedAccount by remember(transaction) { mutableStateOf(accounts.find { it.id == transaction.accountId }) }
    var note by remember(transaction) { mutableStateOf(transaction.note) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAccountPicker by remember { mutableStateOf(false) }

    val categories = if (selectedType == TransactionType.INCOME) INCOME_CATEGORIES else EXPENSE_CATEGORIES

    Scaffold(
        topBar = {
            GenZxTopBar(
                title = "Edit Transaksi",
                onBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Hapus", tint = Color.White)
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Type Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant)
                    .padding(4.dp)
            ) {
                TransactionTypeTab(
                    text = "Pengeluaran",
                    emoji = "📉",
                    selected = selectedType == TransactionType.EXPENSE,
                    selectedColor = ExpenseRed,
                    modifier = Modifier.weight(1f)
                ) { selectedType = TransactionType.EXPENSE; selectedCategory = "" }
                TransactionTypeTab(
                    text = "Pemasukan",
                    emoji = "📈",
                    selected = selectedType == TransactionType.INCOME,
                    selectedColor = IncomeGreen,
                    modifier = Modifier.weight(1f)
                ) { selectedType = TransactionType.INCOME; selectedCategory = "" }
            }

            // Amount
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Jumlah", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it.filter { c -> c.isDigit() } },
                        prefix = { Text("Rp ", style = MaterialTheme.typography.titleLarge, color = Teal) },
                        textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fields
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    GenZxTextField(value = title, onValueChange = { title = it }, label = "Keterangan")
                    HorizontalDivider(color = OutlineLight, modifier = Modifier.padding(vertical = 4.dp))
                    GenZxTextField(value = note, onValueChange = { note = it }, label = "Catatan")
                    HorizontalDivider(color = OutlineLight, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showAccountPicker = true }.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Akun", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedAccount?.name ?: "Pilih akun", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category
            Text("Kategori", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(((categories.size / 4 + 1) * 90).dp)
            ) {
                items(categories) { category ->
                    val emoji = CATEGORY_ICONS[category] ?: "📦"
                    val isSelected = selectedCategory == category
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TealContainer else SurfaceLight)
                            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Teal else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(8.dp)
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                        Text(category.split(" ").first(), style = MaterialTheme.typography.labelSmall, color = if (isSelected) Teal else TextSecondary, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && amount.isNotBlank() && selectedCategory.isNotBlank() && selectedAccount != null) {
                        val updated = transaction.copy(
                            title = title.trim(),
                            amount = amount.toDoubleOrNull() ?: transaction.amount,
                            type = selectedType,
                            category = selectedCategory,
                            accountId = selectedAccount!!.id,
                            note = note.trim()
                        )
                        viewModel.updateTransaksi(transaction, updated)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Transaksi ini akan dihapus permanen dan saldo akun akan disesuaikan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hapusTransaksi(transaction)
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ExpenseRed)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    // Account picker
    if (showAccountPicker) {
        AlertDialog(
            onDismissRequest = { showAccountPicker = false },
            title = { Text("Pilih Akun") },
            text = {
                Column {
                    accounts.forEach { account ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedAccount = account; showAccountPicker = false }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(when (account.type) { AccountType.CASH -> "👛"; AccountType.BANK -> "🏦"; AccountType.SAVINGS -> "💳"; AccountType.EWALLET -> "📱" }, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(account.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(FormatUtil.formatCurrencyShort(account.balance), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            if (account.id == selectedAccount?.id) { Spacer(Modifier.weight(1f)); Icon(Icons.Default.Check, null, tint = Teal) }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
