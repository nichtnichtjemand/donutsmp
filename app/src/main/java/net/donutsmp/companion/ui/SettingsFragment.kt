package net.donutsmp.companion.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.donutsmp.companion.api.ApiClient
import net.donutsmp.companion.api.AuctionSearchRequest
import net.donutsmp.companion.databinding.FragmentSettingsBinding
import net.donutsmp.companion.worker.AuctionMonitorService

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("donutsmp_prefs", Context.MODE_PRIVATE)
        val savedKey = prefs.getString("api_key", "")
        val savedInterval = prefs.getInt("monitoring_interval_seconds", 5)

        binding.etApiKey.setText(savedKey)
        binding.etInterval.setText(savedInterval.toString())

        binding.btnSaveSettings.setOnClickListener {
            val apiKey = binding.etApiKey.text.toString().trim()
            val interval = binding.etInterval.text.toString().toIntOrNull() ?: 5

            prefs.edit()
                .putString("api_key", apiKey)
                .putInt("monitoring_interval_seconds", interval)
                .apply()

            if (apiKey.isNotEmpty()) {
                AuctionMonitorService.stop(requireContext())
                AuctionMonitorService.start(requireContext())
                Toast.makeText(context, "Settings saved. Monitoring started.", Toast.LENGTH_SHORT).show()
            } else {
                AuctionMonitorService.stop(requireContext())
                Toast.makeText(context, "Settings saved. Monitoring stopped (no API key).", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTestConnection.setOnClickListener {
            val apiKey = binding.etApiKey.text.toString().trim()
            if (apiKey.isEmpty()) {
                Toast.makeText(context, "Please enter an API key first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnTestConnection.isEnabled = false
            binding.tvConnectionStatus.text = "Testing..."

            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        ApiClient.create(apiKey).getAuctionList(1, AuctionSearchRequest())
                    }
                    binding.tvConnectionStatus.text = "Connected! Found ${result.result.size} listings."
                } catch (e: Exception) {
                    binding.tvConnectionStatus.text = "Failed: ${e.message}"
                } finally {
                    binding.btnTestConnection.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
