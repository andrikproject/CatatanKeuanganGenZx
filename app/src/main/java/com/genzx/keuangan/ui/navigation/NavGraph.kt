package com.genzx.keuangan.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.genzx.keuangan.ui.screen.home.HomeScreen
import com.genzx.keuangan.ui.screen.jurnal.JurnalScreen
import com.genzx.keuangan.ui.screen.laporan.LaporanScreen
import com.genzx.keuangan.ui.screen.profil.ProfilScreen
import com.genzx.keuangan.ui.screen.transaksi.TambahTransaksiScreen
import com.genzx.keuangan.ui.screen.transaksi.EditTransaksiScreen
import com.genzx.keuangan.ui.screen.akun.KelolaAkunScreen
import com.genzx.keuangan.ui.screen.akun.DetailAkunScreen
import com.genzx.keuangan.ui.screen.akun.TransferScreen
import com.genzx.keuangan.ui.screen.akun.KelolaBudgetScreen
import com.genzx.keuangan.ui.screen.wrapped.WrappedFinansialScreen
import com.genzx.keuangan.ui.viewmodel.MainViewModel

@Composable
fun GenZxNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = mainViewModel,
                onNavigateToTambah = { type ->
                    navController.navigate(Screen.TambahTransaksi.createRoute(type))
                },
                onNavigateToJurnal = {
                    navController.navigate(Screen.Jurnal.route)
                },
                onNavigateToAkun = {
                    navController.navigate(Screen.KelolaAkun.route)
                },
                onNavigateToWrapped = { month, year ->
                    navController.navigate(Screen.WrappedFinansial.createRoute(month, year))
                }
            )
        }

        composable(Screen.Jurnal.route) {
            JurnalScreen(
                viewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditTransaksi.createRoute(id))
                },
                onNavigateToTambah = { type ->
                    navController.navigate(Screen.TambahTransaksi.createRoute(type))
                }
            )
        }

        composable(Screen.Laporan.route) {
            LaporanScreen(
                viewModel = mainViewModel,
                onNavigateToWrapped = { month, year ->
                    navController.navigate(Screen.WrappedFinansial.createRoute(month, year))
                }
            )
        }

        composable(Screen.Profil.route) {
            ProfilScreen(
                viewModel = mainViewModel,
                onNavigateToKelolaAkun = { navController.navigate(Screen.KelolaAkun.route) },
                onNavigateToKelolaBudget = { navController.navigate(Screen.KelolaBudget.route) },
                onNavigateToSetupPin = { navController.navigate(Screen.SetupPin.route) }
            )
        }

        composable(
            route = Screen.TambahTransaksi.route,
            arguments = listOf(navArgument("type") {
                type = NavType.StringType
                defaultValue = "EXPENSE"
            })
        ) { backStack ->
            val type = backStack.arguments?.getString("type") ?: "EXPENSE"
            TambahTransaksiScreen(
                viewModel = mainViewModel,
                initialType = type,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTransaksi.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            EditTransaksiScreen(
                viewModel = mainViewModel,
                transactionId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.KelolaAkun.route) {
            KelolaAkunScreen(
                viewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.DetailAkun.createRoute(id))
                },
                onNavigateToTransfer = { navController.navigate(Screen.Transfer.route) }
            )
        }

        composable(
            route = Screen.DetailAkun.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            DetailAkunScreen(
                viewModel = mainViewModel,
                accountId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Transfer.route) {
            TransferScreen(
                viewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.KelolaBudget.route) {
            KelolaBudgetScreen(
                viewModel = mainViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SetupPin.route) {
            Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                androidx.compose.material3.Text("PIN Lock - Coming Soon")
            }
        }

        composable(
            route = Screen.WrappedFinansial.route,
            arguments = listOf(
                navArgument("month") { type = NavType.IntType },
                navArgument("year") { type = NavType.IntType }
            )
        ) { backStack ->
            val month = backStack.arguments?.getInt("month") ?: 1
            val year = backStack.arguments?.getInt("year") ?: 2024
            WrappedFinansialScreen(
                viewModel = mainViewModel,
                month = month,
                year = year,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
