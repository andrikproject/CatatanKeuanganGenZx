package com.genzx.keuangan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.genzx.keuangan.ui.navigation.GenZxNavGraph
import com.genzx.keuangan.ui.navigation.Screen
import com.genzx.keuangan.ui.theme.*
import com.genzx.keuangan.ui.viewmodel.MainViewModel

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CatatanKeuanganGenZxTheme {
                GenZxMainScreen(mainViewModel)
            }
        }
    }
}

@Composable
fun GenZxMainScreen(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Beranda", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.Jurnal.route, "Jurnal", Icons.Filled.List, Icons.Outlined.List),
        BottomNavItem(Screen.Laporan.route, "Laporan", Icons.Filled.PieChart, Icons.Outlined.PieChart),
        BottomNavItem(Screen.Profil.route, "Profil", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Bottom nav container with shadow + rounded
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 12.dp,
                        color = SurfaceLight
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomNavItems.forEachIndexed { index, item ->
                                // Center FAB placeholder slot
                                if (index == 2) {
                                    Spacer(modifier = Modifier.width(56.dp))
                                }

                                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .then(
                                            if (selected) Modifier.background(OceanLight.copy(alpha = 0.1f))
                                            else Modifier
                                        )
                                        .clickable {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        tint = if (selected) OceanLight else TextHint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        item.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) OceanLight else TextHint,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // FAB di tengah bottom nav
                    Box(
                        modifier = Modifier.align(Alignment.TopCenter).offset(y = (-20).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(OceanLight, OceanBright))
                                )
                                .clickable {
                                    navController.navigate(Screen.TambahTransaksi.createRoute("EXPENSE"))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Tambah",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            GenZxNavGraph(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }
}
