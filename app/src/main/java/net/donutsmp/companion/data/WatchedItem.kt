package net.donutsmp.companion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_items")
data class WatchedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemName: String,
    val priceThreshold: Double,
    val enabled: Boolean = true
)
