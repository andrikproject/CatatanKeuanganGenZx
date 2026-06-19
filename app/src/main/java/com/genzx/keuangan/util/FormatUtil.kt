package com.genzx.keuangan.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object FormatUtil {

    private val idrFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val dateFormatterFull = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    private val dateFormatterShort = DateTimeFormatter.ofPattern("dd MMM", Locale("id", "ID"))
    private val dateFormatterMonth = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun formatCurrency(amount: Double): String {
        return try {
            idrFormat.format(amount).replace("Rp", "Rp ").replace(",00", "")
        } catch (e: Exception) {
            "Rp ${amount.toLong()}"
        }
    }

    fun formatCurrencyShort(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> "Rp ${String.format("%.1f", amount / 1_000_000_000)}M"
            amount >= 1_000_000 -> "Rp ${String.format("%.1f", amount / 1_000_000)}jt"
            amount >= 1_000 -> "Rp ${String.format("%.0f", amount / 1_000)}rb"
            else -> "Rp ${amount.toLong()}"
        }
    }

    fun formatDateShort(isoDate: String): String {
        return try {
            val date = LocalDateTime.parse(isoDate).toLocalDate()
            val today = LocalDate.now()
            when (date) {
                today -> "Hari ini"
                today.minusDays(1) -> "Kemarin"
                else -> date.format(dateFormatterShort)
            }
        } catch (e: Exception) {
            isoDate.take(10)
        }
    }

    fun formatDateFull(isoDate: String): String {
        return try {
            LocalDateTime.parse(isoDate).toLocalDate().format(dateFormatterFull)
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatMonthYear(month: Int, year: Int): String {
        return try {
            LocalDate.of(year, month, 1).format(dateFormatterMonth)
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            "$month/$year"
        }
    }

    fun nowIso(): String = LocalDateTime.now().format(isoFormatter)

    fun toIso(date: LocalDate): String = date.atStartOfDay().format(isoFormatter)

    fun getMonthNames(): List<String> = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    fun generateAiInsight(income: Double, expense: Double, topCategory: String?): String {
        val net = income - expense
        val ratio = if (income > 0) expense / income else 0.0
        return when {
            income == 0.0 && expense == 0.0 -> 
                "📊 Belum ada transaksi bulan ini. Yuk mulai catat! 🌟"
            ratio > 0.9 ->
                "🚨 Wah, pengeluaran hampir menghabiskan semua pemasukan! ${topCategory?.let { "Kategori '$it' perlu dikurangi." } ?: ""} Coba hemat lebih ya!"
            ratio > 0.7 ->
                "⚠️ Pengeluaran cukup tinggi bulan ini. ${topCategory?.let { "Terbesar di '$it'." } ?: ""} Bulan ini agak royal ya... gapapa, hidup sekali 😅"
            net > 0 && ratio < 0.5 ->
                "🎉 Keren banget! Kamu berhasil hemat ${FormatUtil.formatCurrencyShort(net)} bulan ini. Uang kamu kerja, bukan cuma numpuk 💪"
            net > 0 ->
                "👍 Oke nih! Masih surplus ${FormatUtil.formatCurrencyShort(net)}. Keep it up! ✨"
            else ->
                "💪 Deficit ${FormatUtil.formatCurrencyShort(-net)} bulan ini. Cek pengeluaran di kategori '${topCategory ?: "Lainnya"}' biar ga boncos!"
        }
    }
}
