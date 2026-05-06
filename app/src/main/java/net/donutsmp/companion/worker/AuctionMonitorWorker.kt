package net.donutsmp.companion.worker

import android.content.Context
import androidx.work.*
import net.donutsmp.companion.api.ApiClient
import net.donutsmp.companion.api.AuctionSearchRequest
import net.donutsmp.companion.data.AppDatabase
import net.donutsmp.companion.data.PriceAlert
import net.donutsmp.companion.notifications.NotificationHelper
import java.util.concurrent.TimeUnit

class AuctionMonitorWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("donutsmp_prefs", Context.MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", null) ?: return Result.failure()

        val db = AppDatabase.getInstance(applicationContext)
        val watchedItems = db.watchedItemDao().getEnabledWatchedItems()

        if (watchedItems.isEmpty()) return Result.success()

        val apiService = ApiClient.create(apiKey)

        for (watchedItem in watchedItems) {
            try {
                val response = apiService.getAuctionList(
                    page = 1,
                    search = AuctionSearchRequest(
                        search = watchedItem.itemName,
                        sort = "lowest_price"
                    )
                )

                val listings = response.result
                if (listings.isEmpty()) continue

                val lowestEntry = listings.minByOrNull { it.price } ?: continue

                if (lowestEntry.price <= watchedItem.priceThreshold) {
                    NotificationHelper.sendPriceAlert(
                        context = applicationContext,
                        itemName = watchedItem.itemName,
                        currentPrice = lowestEntry.price,
                        threshold = watchedItem.priceThreshold,
                        sellerName = lowestEntry.seller.name
                    )

                    val alert = PriceAlert(
                        itemName = watchedItem.itemName,
                        triggeredPrice = lowestEntry.price,
                        threshold = watchedItem.priceThreshold,
                        sellerName = lowestEntry.seller.name
                    )
                    db.priceAlertDao().insert(alert)
                }
            } catch (e: Exception) {
                // Continue checking other items
            }
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "auction_monitor"

        fun schedule(context: Context, intervalMinutes: Long = 15) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<AuctionMonitorWorker>(
                intervalMinutes, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
