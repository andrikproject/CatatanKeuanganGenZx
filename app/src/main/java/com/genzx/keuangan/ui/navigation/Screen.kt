package com.genzx.keuangan.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Jurnal : Screen("jurnal")
    object Laporan : Screen("laporan")
    object Profil : Screen("profil")
    object TambahTransaksi : Screen("tambah_transaksi?type={type}") {
        fun createRoute(type: String = "EXPENSE") = "tambah_transaksi?type=$type"
    }
    object EditTransaksi : Screen("edit_transaksi/{id}") {
        fun createRoute(id: Long) = "edit_transaksi/$id"
    }
    object DetailAkun : Screen("detail_akun/{id}") {
        fun createRoute(id: Long) = "detail_akun/$id"
    }
    object KelolaAkun : Screen("kelola_akun")
    object KelolaBudget : Screen("kelola_budget")
    object WrappedFinansial : Screen("wrapped_finansial/{month}/{year}") {
        fun createRoute(month: Int, year: Int) = "wrapped_finansial/$month/$year"
    }
    object PinLock : Screen("pin_lock")
    object SetupPin : Screen("setup_pin")
    object Transfer : Screen("transfer")
}
