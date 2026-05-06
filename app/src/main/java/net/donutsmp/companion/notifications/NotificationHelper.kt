package net.donutsmp.companion.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ID = "auction_alerts"
    const val SERVICE_CHANNEL_ID = "auction_monitor_service"
    const val SERVICE_NOTIFICATION_ID = 1
    private var notificationId = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alertChannel = NotificationChannel(
                CHANNEL_ID, "Auction Alerts", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when a watched item exceeds its max price"
            }
            notificationManager.createNotificationChannel(alertChannel)

            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID, "Auction Monitor", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing notification while the auction monitor is running"
            }
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    fun buildServiceNotification(context: Context): android.app.Notification {
        return NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Auction Monitor")
            .setContentText("Monitoring auction prices in the background")
            .setOngoing(true)
            .build()
    }

    fun sendPriceAlert(
        context: Context,
        itemName: String,
        currentPrice: Double,
        threshold: Double,
        sellerName: String
    ) {
        val title = "Price Alert: $itemName"
        val message = "Listed at %.2f (above your max of %.2f) by $sellerName".format(
            currentPrice, threshold
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId++, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
