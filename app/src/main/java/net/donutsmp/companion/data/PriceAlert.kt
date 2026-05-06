package net.donutsmp.companion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemName: String,
    val triggeredPrice: Double,
    val threshold: Double,
    val sellerName: String,
    val timestamp: Long = System.currentTimeMillis()
)
