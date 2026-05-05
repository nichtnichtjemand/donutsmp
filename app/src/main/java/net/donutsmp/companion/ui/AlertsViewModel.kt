package net.donutsmp.companion.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.donutsmp.companion.data.AppDatabase

class AlertsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).priceAlertDao()
    val alerts = dao.getAllAlerts()

    fun clearAlerts() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}
