package net.donutsmp.companion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.donutsmp.companion.databinding.FragmentWatchlistBinding

class WatchlistFragment : Fragment() {
    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WatchlistViewModel by viewModels()
    private lateinit var adapter: WatchlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WatchlistAdapter(
            onDelete = { item -> viewModel.deleteItem(item) },
            onToggle = { item -> viewModel.toggleItem(item) }
        )
        binding.rvWatchlist.adapter = adapter

        viewModel.watchedItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmptyWatchlist.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddItem.setOnClickListener {
            AddWatchItemDialog { name, threshold ->
                viewModel.addItem(name, threshold)
            }.show(childFragmentManager, "add_watch_item")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
