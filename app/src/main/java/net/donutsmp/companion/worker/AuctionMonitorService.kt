package net.donutsmp.companion.worker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.*
import net.donutsmp.companion.api.ApiClient
import net.donutsmp.companion.api.AuctionSearchRequest
import net.donutsmp.companion.data.AppDatabase
import net.donutsmp.companion.data.PriceAlert
import net.donutsmp.companion.notifications.NotificationHelper

class AuctionMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val alertedItems = mutableMapOf<Long, Long>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NotificationHelper.SERVICE_NOTIFICATION_ID,
            NotificationHelper.buildServiceNotification(this)
        )

        val prefs = getSharedPreferences("donutsmp_prefs", Context.MODE_PRIVATE)
        val intervalSeconds = prefs.getInt("monitoring_interval_seconds", DEFAULT_INTERVAL.toInt()).toLong()

        serviceScope.launch {
            while (isActive) {
                try {
                    checkAuctions()
                } catch (e: Exception) {
                    // Continue on unexpected errors
                }
                delay(intervalSeconds * 1000L)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private suspend fun checkAuctions() {
        val prefs = getSharedPreferences("donutsmp_prefs", Context.MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", null) ?: return

        val db = AppDatabase.getInstance(applicationContext)
        val watchedItems = db.watchedItemDao().getEnabledWatchedItems()

        if (watchedItems.isEmpty()) return

        val apiService = ApiClient.create(apiKey)

        for (watchedItem in watchedItems) {
            try {
                val response = apiService.getAuctionList(
                    page = 1,
                    search = AuctionSearchRequest(
                        search = watchedItem.itemName,
                        sort = "highest_price"
                    )
                )

                val listings = response.result
                if (listings.isEmpty()) continue

                val highestEntry = listings.maxByOrNull { it.price } ?: continue

                val now = System.currentTimeMillis()
                val lastAlerted = alertedItems[watchedItem.id] ?: 0L
                if (highestEntry.price >= watchedItem.priceThreshold &&
                    (now - lastAlerted) > ALERT_COOLDOWN_MS
                ) {
                    alertedItems[watchedItem.id] = now
                    NotificationHelper.sendPriceAlert(
                        context = applicationContext,
                        itemName = watchedItem.itemName,
                        currentPrice = highestEntry.price,
                        threshold = watchedItem.priceThreshold,
                        sellerName = highestEntry.seller.name
                    )

                    val alert = PriceAlert(
                        itemName = watchedItem.itemName,
                        triggeredPrice = highestEntry.price,
                        threshold = watchedItem.priceThreshold,
                        sellerName = highestEntry.seller.name
                    )
                    db.priceAlertDao().insert(alert)
                }
            } catch (e: Exception) {
                // Continue checking other items
            }
        }
    }

    companion object {
        const val DEFAULT_INTERVAL = 5L
        private const val ALERT_COOLDOWN_MS = 60_000L

        fun start(context: Context) {
            val intent = Intent(context, AuctionMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AuctionMonitorService::class.java))
        }
    }
}
