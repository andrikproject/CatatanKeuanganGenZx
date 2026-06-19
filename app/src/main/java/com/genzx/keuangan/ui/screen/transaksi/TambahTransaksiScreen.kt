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
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahTransaksiScreen(
    viewModel: MainViewModel,
    initialType: String = "EXPENSE",
    onNavigateBack: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    var selectedType by remember { mutableStateOf(if (initialType == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAccountPicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts.isNotEmpty()) {
            selectedAccount = accounts.firstOrNull { it.isDefault } ?: accounts.first()
        }
    }

    val categories = if (selectedType == TransactionType.INCOME) INCOME_CATEGORIES else EXPENSE_CATEGORIES

    Scaffold(
        topBar = {
            GenZxTopBar(
                title = if (selectedType == TransactionType.INCOME) "Tambah Pemasukan" else "Tambah Pengeluaran",
                onBack = onNavigateBack
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
                ) {
                    selectedType = TransactionType.EXPENSE
                    selectedCategory = ""
                }
                TransactionTypeTab(
                    text = "Pemasukan",
                    emoji = "📈",
                    selected = selectedType == TransactionType.INCOME,
                    selectedColor = IncomeGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    selectedType = TransactionType.INCOME
                    selectedCategory = ""
                }
            }

            // Amount Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Jumlah",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' }; amountError = false },
                        placeholder = { Text("0", style = MaterialTheme.typography.displaySmall) },
                        prefix = { Text("Rp ", style = MaterialTheme.typography.titleLarge, color = if (amountError) ExpenseRed else Teal) },
                        textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = amountError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title, Note, Account, Date
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    GenZxTextField(
                        value = title,
                        onValueChange = { title = it; titleError = false },
                        label = "Keterangan",
                        placeholder = "Contoh: Makan siang, Gaji bulan ini...",
                        isError = titleError
                    )
                    HorizontalDivider(color = OutlineLight, modifier = Modifier.padding(vertical = 4.dp))
                    GenZxTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = "Catatan (opsional)",
                        placeholder = "Tambah catatan..."
                    )
                    HorizontalDivider(color = OutlineLight, modifier = Modifier.padding(vertical = 4.dp))

                    // Account Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAccountPicker = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Akun", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedAccount?.name ?: "Pilih akun",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }

                    HorizontalDivider(color = OutlineLight, modifier = Modifier.padding(vertical = 4.dp))

                    // Date Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tanggal", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = FormatUtil.formatDateFull(FormatUtil.toIso(selectedDate)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Grid
            Text(
                text = "Kategori",
                style = MaterialTheme.typography.headlineSmall,
                color = if (categoryError) ExpenseRed else TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

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
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Teal else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = category; categoryError = false }
                            .padding(8.dp)
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = category.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Teal else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    titleError = title.isBlank()
                    amountError = amount.isBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0
                    categoryError = selectedCategory.isBlank()
                    if (!titleError && !amountError && !categoryError && selectedAccount != null) {
                        viewModel.tambahTransaksi(
                            Transaction(
                                title = title.trim(),
                                amount = amount.toDouble(),
                                type = selectedType,
                                category = selectedCategory,
                                accountId = selectedAccount!!.id,
                                note = note.trim(),
                                date = FormatUtil.toIso(selectedDate)
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.INCOME) IncomeGreen else Teal
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simpan Transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Account Picker Dialog
    if (showAccountPicker) {
        AlertDialog(
            onDismissRequest = { showAccountPicker = false },
            title = { Text("Pilih Akun") },
            text = {
                Column {
                    accounts.forEach { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAccount = account; showAccountPicker = false }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (account.type) {
                                    AccountType.CASH -> "👛"
                                    AccountType.BANK -> "🏦"
                                    AccountType.SAVINGS -> "💳"
                                    AccountType.EWALLET -> "📱"
                                },
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(account.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(FormatUtil.formatCurrencyShort(account.balance), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            if (account.id == selectedAccount?.id) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.Check, null, tint = Teal)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun GenZxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isError: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = if (isError) ExpenseRed else TextSecondary)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = TextHint)
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun TransactionTypeTab(
    text: String,
    emoji: String,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) selectedColor else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
