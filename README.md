# DonutSMP Companion

An Android companion app for the DonutSMP Minecraft server that monitors the auction house and sends price-drop notifications.

## Features

- **Watchlist** – Add items you want to track and set a price threshold for each one
- **Price Alerts** – Browse a history of all triggered price-drop notifications
- **Settings** – Enter your DonutSMP API key and configure how often the auction house is polled (default: every 15 minutes)
- **Background Monitoring** – Uses WorkManager to periodically check the DonutSMP auction API even when the app is closed; sends an Android notification whenever a watched item drops below its threshold

## Tech Stack

- Kotlin + Android Jetpack (ViewModel, LiveData, Room, WorkManager)
- Retrofit 2 + OkHttp for API calls
- Material Components for the UI (bottom navigation, FAB, cards)
- View Binding

## Getting Started

1. Clone the repository and open it in Android Studio.
2. Build and install the app on a device or emulator running Android 7.0+ (API 24).
3. Open the **Settings** tab, enter your DonutSMP API key, and save.
4. Switch to the **Watchlist** tab and add the items you want to monitor.

## Project Structure

```
app/src/main/java/net/donutsmp/companion/
├── api/          – Retrofit service and data models for the DonutSMP API
├── data/         – Room database, DAOs, and entities (WatchedItem, PriceAlert)
├── notifications/– NotificationHelper for posting price-alert notifications
├── ui/           – Fragments, ViewModels, and RecyclerView adapters
└── worker/       – AuctionMonitorWorker (background WorkManager task)
```