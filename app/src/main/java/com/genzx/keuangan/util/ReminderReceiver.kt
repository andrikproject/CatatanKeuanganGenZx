package com.genzx.keuangan.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages = listOf(
            "Jangan lupa catat pengeluaran hari ini! 📝",
            "Udah catat keuangan hari ini belum? 👀",
            "Yuk update catatan keuanganmu! 💰",
            "Sedikit catatan hari ini = finansial lebih sehat ✨",
            "Biar ga boncos, catat dulu yuk! 🌟"
        )
        val message = messages.random()

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Catatan Keuangan GenZx")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(1001, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
