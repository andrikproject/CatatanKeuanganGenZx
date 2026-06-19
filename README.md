# Catatan Keuangan GenZx

Aplikasi catatan keuangan pribadi modern dengan UI/UX GenZ — dibuat dengan Kotlin + Jetpack Compose.

## Fitur

- 💰 Catat pemasukan & pengeluaran
- 📊 Laporan & grafik kategori bulanan
- 🏦 Multi akun (Cash, Bank, E-Wallet, Tabungan)
- 🔄 Transfer antar akun
- 🎯 Budget per kategori
- 🤖 AI Insight otomatis
- 🎉 Rekap Finansial (Wrapped) ala Spotify
- 🔒 PIN Lock keamanan
- 👁️ Hide/show saldo
- 🔔 Notifikasi pengingat harian

## Tech Stack

- Kotlin + Jetpack Compose
- Room Database
- MVVM Architecture
- Material Design 3
- GitHub Actions CI/CD

## Build

```bash
# Clone
git clone https://github.com/username/CatatanKeuanganGenZx.git

# Build debug APK
./gradlew assembleDebug

# APK output
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions

Setiap push ke `main` akan otomatis build APK dan tersedia di tab **Actions > Artifacts**.

## Package

`com.genzx.keuangan`
