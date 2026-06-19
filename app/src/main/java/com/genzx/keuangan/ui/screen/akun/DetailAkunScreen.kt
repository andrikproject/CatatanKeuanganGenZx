package com.genzx.keuangan.ui.screen.akun

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
import com.genzx.keuangan.domain.model.*
import com.genzx.keuangan.ui.components.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAkunScreen(
    viewModel: MainViewModel,
    accountId: Long,
    onNavigateBack: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    val account = accounts.find { it.id == accountId }
    val allTransactions by viewModel.repository.getAllTransactions().collectAsState(emptyList())
    val accountTransactions = allTransactions.filter { it.accountId == accountId }

    if (account == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Teal)
        }
        return
    }

    val accountColor = try { Color(android.graphics.Color.parseColor(account.color)) } catch (e: Exception) { Teal }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(account.name, style = MaterialTheme.typography.headlineMedium, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = accountColor)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Account hero card
                Box(
                    modifier = Modifier.fillMaxWidth().background(accountColor).padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (account.type) {
                                    AccountType.CASH -> "👛"; AccountType.BANK -> "🏦"
                                    AccountType.SAVINGS -> "💳"; AccountType.EWALLET -> "📱"
                                },
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = account.type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                if (account.isDefault) {
                                    Text("Akun Default ⭐", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Saldo", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Text(
                            text = FormatUtil.formatCurrency(account.balance),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${accountTransactions.size} transaksi total",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            item { SectionHeader(title = "Riwayat Transaksi") }

            if (accountTransactions.isEmpty()) {
                item {
                    EmptyState(emoji = "💸", title = "Belum ada transaksi", subtitle = "Transaksi yang menggunakan akun ini akan muncul di sini")
                }
            } else {
                items(accountTransactions) { transaction ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    var fromAccount by remember { mutableStateOf<Account?>(null) }
    var toAccount by remember { mutableStateOf<Account?>(null) }
    var amount by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(accounts) {
        if (fromAccount == null && accounts.size >= 1) fromAccount = accounts[0]
        if (toAccount == null && accounts.size >= 2) toAccount = accounts[1]
    }

    Scaffold(
        topBar = {
            GenZxTopBar(title = "🔄 Transfer", onBack = onNavigateBack)
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // From account
            Text("Dari Akun", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            AkunDropdown(
                accounts = accounts,
                selected = fromAccount,
                onSelect = { fromAccount = it }
            )

            // Swap icon
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = { val tmp = fromAccount; fromAccount = toAccount; toAccount = tmp },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(TealContainer)
                ) {
                    Icon(Icons.Default.SwapVert, "Swap", tint = Teal)
                }
            }

            // To account
            Text("Ke Akun", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            AkunDropdown(
                accounts = accounts,
                selected = toAccount,
                onSelect = { toAccount = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah Transfer") },
                prefix = { Text("Rp ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val from = fromAccount
                    val to = toAccount
                    val amt = amount.toDoubleOrNull()
                    if (from != null && to != null && amt != null && amt > 0 && from.id != to.id) {
                        viewModel.transfer(from.id, to.id, amt)
                        showSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Icon(Icons.Default.SwapHoriz, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Transfer Sekarang", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false; onNavigateBack() },
            title = { Text("🎉 Transfer Berhasil!") },
            text = { Text("Rp ${amount} berhasil ditransfer dari ${fromAccount?.name} ke ${toAccount?.name}.") },
            confirmButton = {
                Button(onClick = { showSuccess = false; onNavigateBack() }, colors = ButtonDefaults.buttonColors(containerColor = Teal)) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun AkunDropdown(
    accounts: List<Account>,
    selected: Account?,
    onSelect: (Account) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (selected?.type) {
                            AccountType.CASH -> "👛"; AccountType.BANK -> "🏦"
                            AccountType.SAVINGS -> "💳"; AccountType.EWALLET -> "📱"; null -> "💳"
                        },
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(selected?.name ?: "Pilih akun", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        if (selected != null) Text(FormatUtil.formatCurrencyShort(selected.balance), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                Icon(Icons.Default.ExpandMore, null, tint = TextSecondary)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(when (account.type) { AccountType.CASH -> "👛"; AccountType.BANK -> "🏦"; AccountType.SAVINGS -> "💳"; AccountType.EWALLET -> "📱" }, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(account.name, style = MaterialTheme.typography.bodyMedium)
                                Text(FormatUtil.formatCurrencyShort(account.balance), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    },
                    onClick = { onSelect(account); expanded = false }
                )
            }
        }
    }
}
