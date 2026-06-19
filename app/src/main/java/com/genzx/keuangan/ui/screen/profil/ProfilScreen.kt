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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
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
    val net = state.monthlyIncome - state.monthlyExpense
    val savingRate = if (state.monthlyIncome > 0) (net / state.monthlyIncome * 100).toInt() else 0

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header dengan gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        Brush.linearGradient(
                            listOf(GradientStart, GradientMid, GradientEnd),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        )
                    )
                    drawCircle(
                        Color.White.copy(alpha = 0.05f),
                        radius = size.width * 0.5f,
                        center = Offset(size.width * 0.85f, -size.height * 0.1f)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 32.sp)
                        }
                        Column {
                            Text(
                                "GenZxer 👋",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${now.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale("id")))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge(state.accounts.size.toString(), "Akun")
                        StatBadge(state.budgets.size.toString(), "Budget")
                        StatBadge("$savingRate%", "Saving Rate")
                        StatBadge(state.recentTransactions.size.toString() + "+", "Transaksi")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Insight
            val insight = FormatUtil.generateAiInsight(state.monthlyIncome, state.monthlyExpense, null)
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VioletContainer),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Text("🤖", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "AI Insight Bulan Ini ✨",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnVioletContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            insight,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnVioletContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Menu: Kelola Keuangan
            ProfilMenuSection(title = "💰 Kelola Keuangan") {
                ProfilMenuItem(
                    icon = Icons.Outlined.AccountBalance,
                    label = "Kelola Akun",
                    subtitle = "${state.accounts.size} akun terdaftar",
                    color = OceanLight,
                    onClick = onNavigateToKelolaAkun
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = OutlineLight)
                ProfilMenuItem(
                    icon = Icons.Outlined.PieChart,
                    label = "Kelola Budget",
                    subtitle = "${state.budgets.size} budget aktif",
                    color = VioletAccent,
                    onClick = onNavigateToKelolaBudget
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Menu: Keamanan
            ProfilMenuSection(title = "🔒 Keamanan") {
                ProfilMenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "PIN Lock",
                    subtitle = "Lindungi aplikasi dengan PIN 4 digit",
                    color = ExpenseRed,
                    onClick = onNavigateToSetupPin
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Menu: Informasi
            ProfilMenuSection(title = "ℹ️ Informasi") {
                ProfilMenuItem(
                    icon = Icons.Outlined.Info,
                    label = "Tentang Aplikasi",
                    subtitle = "Catatan Keuangan GenZx",
                    color = OceanBright,
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = OutlineLight)
                ProfilMenuItem(
                    icon = Icons.Outlined.Star,
                    label = "Versi Aplikasi",
                    subtitle = "v1.0.0 - Built with ❤️ by GenZx",
                    color = AmberAccent,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.7f)
        )
    }
}

@Composable
fun ProfilMenuSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ProfilMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            null,
            tint = TextHint,
            modifier = Modifier.size(20.dp)
        )
    }
}
