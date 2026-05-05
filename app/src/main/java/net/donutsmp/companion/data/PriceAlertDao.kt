package net.donutsmp.companion.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts ORDER BY timestamp DESC LIMIT 100")
    fun getAllAlerts(): LiveData<List<PriceAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: PriceAlert)

    @Query("DELETE FROM price_alerts WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)

    @Query("DELETE FROM price_alerts")
    suspend fun deleteAll()
}
