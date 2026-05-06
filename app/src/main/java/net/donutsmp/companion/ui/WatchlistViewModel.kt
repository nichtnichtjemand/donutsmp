package net.donutsmp.companion.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.donutsmp.companion.data.AppDatabase
import net.donutsmp.companion.data.WatchedItem

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).watchedItemDao()
    val watchedItems = dao.getAllWatchedItems()

    fun addItem(itemName: String, priceThreshold: Double) {
        viewModelScope.launch {
            dao.insert(WatchedItem(itemName = itemName, priceThreshold = priceThreshold))
        }
    }

    fun deleteItem(item: WatchedItem) {
        viewModelScope.launch {
            dao.delete(item)
        }
    }

    fun toggleItem(item: WatchedItem) {
        viewModelScope.launch {
            dao.update(item.copy(enabled = !item.enabled))
        }
    }
}
