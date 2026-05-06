package net.donutsmp.companion.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WatchedItemDao {
    @Query("SELECT * FROM watched_items ORDER BY itemName ASC")
    fun getAllWatchedItems(): LiveData<List<WatchedItem>>

    @Query("SELECT * FROM watched_items WHERE enabled = 1")
    suspend fun getEnabledWatchedItems(): List<WatchedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchedItem)

    @Update
    suspend fun update(item: WatchedItem)

    @Delete
    suspend fun delete(item: WatchedItem)

    @Query("DELETE FROM watched_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
