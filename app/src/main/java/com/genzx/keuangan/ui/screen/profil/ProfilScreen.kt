package com.genzx.keuangan.ui.screen.profil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel
import com.genzx.keuangan.util.FormatUtil
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    viewModel: MainViewModel,
    onNavigateToKelolaAkun: () -> Unit,
    onNavigateToKelolaBudget: () -> Unit,
    onNavigateToSetupPin: () -> Unit
) {
    val state by viewModel.homeState.collectAsState()
    val now = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👤 Profil", style = MaterialTheme.typography.headlineMedium, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Teal)
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
            // Profile header
            Box(
                modifier = Modifier.fillMaxWidth().background(Teal).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("GenZx User 👋", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Member sejak ${now.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        ProfileStat(
                            label = "Akun",
                            value = state.accounts.size.toString()
                        )
                        ProfileStat(
                            label = "Budget",
                            value = state.budgets.size.toString()
                        )
                        ProfileStat(
                            label = "Transaksi",
                            value = state.recentTransactions.size.toString() + "+"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Insight card
            val insight = FormatUtil.generateAiInsight(
                state.monthlyIncome,
                state.monthlyExpense,
                null
            )
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TealContainer)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Text("🤖", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("AI Insight Bulan Ini", style = MaterialTheme.typography.labelMedium, color = TealDark, fontWeight = FontWeight.SemiBold)
                        Text(insight, style = MaterialTheme.typography.bodySmall, color = OnTealContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu sections
            MenuSection(title = "Kelola Keuangan") {
                MenuItem(
                    icon = Icons.Outlined.AccountBox,
                    label = "Kelola Akun",
                    subtitle = "${state.accounts.size} akun terdaftar",
                    onClick = onNavigateToKelolaAkun
                )
                MenuItem(
                    icon = Icons.Outlined.MonetizationOn,
                    label = "Kelola Budget",
                    subtitle = "Atur batas pengeluaran per kategori",
                    onClick = onNavigateToKelolaBudget
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            MenuSection(title = "Keamanan") {
                MenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "PIN Lock",
                    subtitle = "Lindungi aplikasi dengan PIN",
                    onClick = onNavigateToSetupPin
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            MenuSection(title = "Informasi") {
                MenuItem(
                    icon = Icons.Outlined.Info,
                    label = "Tentang Aplikasi",
                    subtitle = "Catatan Keuangan GenZx v1.0.0",
                    onClick = {}
                )
                MenuItem(
                    icon = Icons.Outlined.Info,
                    label = "Versi",
                    subtitle = "1.0.0 - Built with ❤️",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun MenuSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            content()
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(TealContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Teal, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextPrimary)
            if (subtitle.isNotEmpty()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}
