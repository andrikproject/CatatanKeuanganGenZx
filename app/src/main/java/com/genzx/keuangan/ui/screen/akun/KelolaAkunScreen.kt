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
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaAkunScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToTransfer: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }
    val totalBalance = accounts.sumOf { it.balance }

    Scaffold(
        topBar = {
            GenZxTopBar(
                title = "Kelola Akun",
                onBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToTransfer) {
                        Icon(Icons.Default.SwapHoriz, "Transfer", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Teal,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Tambah Akun")
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total balance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Teal)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Semua Akun", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtil.formatCurrency(totalBalance),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (accounts.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "💳",
                        title = "Belum ada akun",
                        subtitle = "Tambah akun pertamamu!"
                    )
                }
            } else {
                items(accounts) { account ->
                    AkunItem(
                        account = account,
                        onClick = { onNavigateToDetail(account.id) },
                        onSetDefault = { viewModel.setDefaultAkun(account.id) },
                        onDelete = { accountToDelete = account }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        TambahAkunDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { account ->
                viewModel.tambahAkun(account)
                showAddDialog = false
            }
        )
    }

    accountToDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text("Hapus Akun?") },
            text = { Text("Akun \"${account.name}\" akan dihapus. Transaksi terkait tidak akan terhapus.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.hapusAkun(account); accountToDelete = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = ExpenseRed)
                ) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { accountToDelete = null }) { Text("Batal") } }
        )
    }
}

@Composable
fun AkunItem(
    account: Account,
    onClick: () -> Unit,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    val accountColor = try { Color(android.graphics.Color.parseColor(account.color)) } catch (e: Exception) { Teal }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(accountColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (account.type) {
                        AccountType.CASH -> "👛"; AccountType.BANK -> "🏦"
                        AccountType.SAVINGS -> "💳"; AccountType.EWALLET -> "📱"
                    },
                    fontSize = 22.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(account.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    if (account.isDefault) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(TealContainer).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Default", style = MaterialTheme.typography.labelSmall, color = Teal)
                        }
                    }
                }
                Text(
                    text = account.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FormatUtil.formatCurrencyShort(account.balance),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (account.balance >= 0) TextPrimary else ExpenseRed
                )
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        if (!account.isDefault) {
                            DropdownMenuItem(
                                text = { Text("Jadikan Default") },
                                onClick = { onSetDefault(); showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Star, null, tint = Teal) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Hapus", color = ExpenseRed) },
                            onClick = { onDelete(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = ExpenseRed) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TambahAkunDialog(
    onDismiss: () -> Unit,
    onConfirm: (Account) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.CASH) }
    var initialBalance by remember { mutableStateOf("0") }
    var selectedColor by remember { mutableStateOf("#006B7A") }
    val types = AccountType.values()
    val colorOptions = listOf("#006B7A", "#7C4DFF", "#00BCD4", "#FF4081", "#FF6D00", "#43A047")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("💳 Tambah Akun", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Akun") },
                    placeholder = { Text("Contoh: BCA, Dompet, Dana") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal)
                )

                Text("Tipe Akun", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = {
                                Text(
                                    when (type) { AccountType.CASH -> "👛 Cash"; AccountType.BANK -> "🏦 Bank"; AccountType.SAVINGS -> "💳 Tabungan"; AccountType.EWALLET -> "📱 E-Wallet" },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Teal, selectedLabelColor = Color.White)
                        )
                    }
                }

                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = { initialBalance = it.filter { c -> c.isDigit() } },
                    label = { Text("Saldo Awal") },
                    prefix = { Text("Rp ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal)
                )

                Text("Warna", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { colorHex ->
                        val c = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Teal }
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                                .background(c)
                                .border(if (selectedColor == colorHex) 3.dp else 0.dp, Color.White, CircleShape)
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            Account(
                                name = name.trim(),
                                type = selectedType,
                                balance = initialBalance.toDoubleOrNull() ?: 0.0,
                                color = selectedColor,
                                createdAt = LocalDateTime.now().toString()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) { Text("Tambah") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
