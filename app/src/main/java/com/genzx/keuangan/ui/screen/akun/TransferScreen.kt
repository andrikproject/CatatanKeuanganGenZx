package com.genzx.keuangan.ui.screen.akun

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.genzx.keuangan.ui.theme.Teal
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fromAccountId by remember { mutableStateOf<Long?>(null) }
    var toAccountId by remember { mutableStateOf<Long?>(null) }
    var amount by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val fromAccount = accounts.find { it.id == fromAccountId }
    val toAccount = accounts.find { it.id == toAccountId }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Transfer Antar Akun") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dari Akun
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = it }
            ) {
                OutlinedTextField(
                    value = fromAccount?.name ?: "Pilih akun asal",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dari Akun") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = fromExpanded,
                    onDismissRequest = { fromExpanded = false }
                ) {
                    accounts.filter { it.id != toAccountId }.forEach { acc ->
                        DropdownMenuItem(
                            text = { Text("${acc.name} (Rp ${acc.balance})") },
                            onClick = {
                                fromAccountId = acc.id
                                fromExpanded = false
                            }
                        )
                    }
                }
            }

            // Ke Akun
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = it }
            ) {
                OutlinedTextField(
                    value = toAccount?.name ?: "Pilih akun tujuan",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ke Akun") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false }
                ) {
                    accounts.filter { it.id != fromAccountId }.forEach { acc ->
                        DropdownMenuItem(
                            text = { Text("${acc.name} (Rp ${acc.balance})") },
                            onClick = {
                                toAccountId = acc.id
                                toExpanded = false
                            }
                        )
                    }
                }
            }

            // Jumlah
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah Transfer") },
                prefix = { Text("Rp ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Catatan
            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amt = amount.toLongOrNull() ?: 0L
                    if (fromAccountId == null || toAccountId == null) {
                        scope.launch { snackbarHostState.showSnackbar("Pilih akun asal dan tujuan") }
                        return@Button
                    }
                    if (amt <= 0) {
                        scope.launch { snackbarHostState.showSnackbar("Masukkan jumlah yang valid") }
                        return@Button
                    }
                    scope.launch {
                        viewModel.transfer(
                            fromId = fromAccountId!!,
                            toId = toAccountId!!,
                            amount = amt.toDouble()
                        )
                        snackbarHostState.showSnackbar("Transfer berhasil")
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Text("Transfer Sekarang")
            }
        }
    }
}
