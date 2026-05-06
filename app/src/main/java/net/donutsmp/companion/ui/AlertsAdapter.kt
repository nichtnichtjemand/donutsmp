package net.donutsmp.companion.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.donutsmp.companion.data.PriceAlert
import net.donutsmp.companion.databinding.ItemAlertBinding
import java.text.SimpleDateFormat
import java.util.*

class AlertsAdapter : ListAdapter<PriceAlert, AlertsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alert: PriceAlert) {
            binding.tvAlertItemName.text = alert.itemName
            binding.tvAlertPrice.text = "Price: %.2f (threshold: %.2f)".format(
                alert.triggeredPrice, alert.threshold
            )
            binding.tvAlertSeller.text = "Seller: ${alert.sellerName}"
            val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            binding.tvAlertTime.text = sdf.format(Date(alert.timestamp))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PriceAlert>() {
        override fun areItemsTheSame(oldItem: PriceAlert, newItem: PriceAlert) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PriceAlert, newItem: PriceAlert) =
            oldItem == newItem
    }
}
