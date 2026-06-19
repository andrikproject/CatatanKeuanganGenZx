package com.genzx.keuangan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    tonalElevation = 8.dp
                ) {
                    // Regular nav items
                    bottomNavItems.forEachIndexed { index, item ->
                        if (index == 2) {
                            // FAB placeholder
                            NavigationBarItem(
                                selected = false,
                                onClick = {},
                                icon = { Spacer(modifier = Modifier.width(56.dp)) },
                                label = { Spacer(modifier = Modifier.width(56.dp)) }
                            )
                        }

                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Teal,
                                selectedTextColor = Teal,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = TealContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.TambahTransaksi.createRoute("EXPENSE")) },
                    containerColor = Teal,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah Transaksi")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            GenZxNavGraph(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }
}
