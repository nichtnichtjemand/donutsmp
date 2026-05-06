package net.donutsmp.companion.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.donutsmp.companion.data.WatchedItem
import net.donutsmp.companion.databinding.ItemWatchedBinding

class WatchlistAdapter(
    private val onDelete: (WatchedItem) -> Unit,
    private val onToggle: (WatchedItem) -> Unit
) : ListAdapter<WatchedItem, WatchlistAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWatchedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemWatchedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WatchedItem) {
            binding.tvItemName.text = item.itemName
            binding.tvThreshold.text = "Max Price: %.2f".format(item.priceThreshold)
            binding.switchEnabled.isChecked = item.enabled
            binding.switchEnabled.setOnCheckedChangeListener(null)
            binding.switchEnabled.setOnCheckedChangeListener { _, _ -> onToggle(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WatchedItem>() {
        override fun areItemsTheSame(oldItem: WatchedItem, newItem: WatchedItem) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: WatchedItem, newItem: WatchedItem) =
            oldItem == newItem
    }
}
