# TileIQ Inventory - Showroom Intelligence & Dead-Stock Platform

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Storage-Room%20%2B%20Firestore-orange.svg)](https://firebase.google.com/)

TileIQ is a modern Android mobile platform engineered for ceramic tile, marble, and sanitaryware showroom owners. It streamlines dead-stock liquidation, dynamic discount generation, customer quotations with WhatsApp sharing, Google Pay (UPI) payment collection, and multi-branch stock sync.

---

## Key Features

- **Dead-Stock Intelligence**: Automatically detects aging tile inventory (>90 and >180 days) and calculates discount recommendations to recover tied-up capital.
- **Quotation Builder & WhatsApp Export**: Generate customer tile estimates with GST breakdowns, freight, and tile coverage calculation, and share directly via WhatsApp.
- **Google Pay & UPI Integration**: Instant UPI deep links (`upi://pay`) and on-screen QR codes for customer deposits and subscription renewals.
- **Dual Persistence Architecture**:
  - **Local (Offline-first)**: Android Room SQLite database for instant offline access.
  - **Cloud (Real-time)**: Firebase Cloud Firestore synchronization for cross-device and multi-staff showroom management.
- **Showroom Pro Subscription**: ₹5,000 / month (all inclusive of taxes) with integrated GPay renewal and invoice generator.
- **Material Design 3**: Dark-mode showroom aesthetic tailored for touchscreens, foldables, and tablets.

---

## Project Structure

```
├── app/
│   ├── src/main/java/com/example/
│   │   ├── data/
│   │   │   ├── AppDatabase.kt               # Room database configuration
│   │   │   ├── TileDao.kt                   # Data Access Object for tiles & stock
│   │   │   ├── TileItemEntity.kt            # Tile inventory model
│   │   │   ├── QuotationEntity.kt           # Quotations model
│   │   │   ├── StockMovementEntity.kt       # Inventory audit trail
│   │   │   ├── PaymentTransactionEntity.kt  # Google Pay / UPI transaction records
│   │   │   └── FirestoreRepository.kt       # Real-time Firebase Cloud sync
│   │   ├── ui/
│   │   │   ├── screens/                     # Jetpack Compose UI screens
│   │   │   │   ├── DashboardScreen.kt       # Overview, KPIs, dead-stock alert
│   │   │   │   ├── InventoryScreen.kt       # Stock list, filters, tile card details
│   │   │   │   ├── QuotationBuilderScreen.kt# Quotation calculator & WhatsApp export
│   │   │   │   ├── SubscriptionSettingsScreen.kt # Showroom Pro plan & GPay billing
│   │   │   │   └── LoginScreen.kt           # Authentication & showroom selector
│   │   │   ├── TileViewModel.kt             # Unidirectional state management
│   │   │   └── theme/                       # Modern M3 styling & palette
│   │   └── util/
│   │       ├── CurrencyFormatter.kt         # INR currency formatting (₹)
│   │       └── GooglePayUpiHelper.kt        # UPI deep-link and QR generator
│   └── build.gradle.kts                     # App-level dependencies & plugins
├── gradle/
│   └── libs.versions.toml                   # Gradle Version Catalog
└── settings.gradle.kts                      # Gradle root settings
```

---

## Building & Running

### Prerequisites
- Android Studio Ladybug (2024.2+) or higher
- Android SDK 34 / 35
- JDK 17 or 21

### Local Setup
1. Clone this repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/tileiq-inventory-android.git
   cd tileiq-inventory-android
   ```
2. Open the project in Android Studio.
3. Allow Gradle to sync dependencies.
4. Run on an Android Emulator (API 30+) or a physical Android device.

### Building Release APK & Android App Bundle (AAB)
To build a production release APK for distribution:
```bash
./gradlew assembleRelease
```
To generate an Android App Bundle (.aab) for Google Play:
```bash
./gradlew bundleRelease
```

---

## Environment & Secrets Configuration

Sensitive keys (e.g. `GEMINI_API_KEY`, Firebase configurations) are managed via Android `BuildConfig` and `.env` to prevent committing secrets to source control.

Copy `.env.example` to `.env` if custom keys are needed:
```env
# GEMINI_API_KEY=your_key_here
```
*(Note: `.env`, `debug.keystore`, and build artifacts are strictly ignored in `.gitignore`)*

---

## Deployment & Hosting Details

- **Mobile App**: Distributed as an Android APK or via Google Play Store (AAB).
- **Web Streaming Preview**: Accessible on-demand via the Google AI Studio browser streaming emulator.
- **GitHub Pages Web Edition**: A standalone web app is kept separate in the `/docs` folder, ready for 1-click GitHub Pages deployment!

---

## 🌐 Hosting via GitHub Pages (Browser Web Edition)

This repository includes a standalone web edition located in the `/docs` folder that works in any modern browser on **Desktop, iOS (Safari), and Android (Chrome)** with responsive touch controls, WhatsApp quotation sharing, and UPI / Google Pay deep linking.

### How to Enable GitHub Pages in 1 Minute:
1. Go to your GitHub Repository in your browser.
2. Click **Settings** (tab at the top) → Select **Pages** (in the left sidebar).
3. Under **Build and deployment**:
   - **Source**: Select `Deploy from a branch`
   - **Branch**: Select `main` (or `master`)
   - **Folder**: Select `/docs` (not `/root`)
4. Click **Save**.
5. Within 1-2 minutes, GitHub will give you a live URL:
   `https://<your-username>.github.io/<repo-name>/`
   *(Alternatively, an automated GitHub Actions workflow is provided in `.github/workflows/deploy-pages.yml` if you choose GitHub Actions as the source)*.
