package net.donutsmp.companion.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class AddWatchItemDialog(
    private val onAdd: (itemName: String, threshold: Double) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }

        val etItemName = EditText(context).apply {
            hint = "Item name (e.g., Diamond)"
        }
        val etThreshold = EditText(context).apply {
            hint = "Price threshold"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(etItemName)
        layout.addView(etThreshold)

        return AlertDialog.Builder(context)
            .setTitle("Add Watched Item")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = etItemName.text.toString().trim()
                val threshold = etThreshold.text.toString().toDoubleOrNull()

                if (name.isEmpty()) {
                    Toast.makeText(context, "Item name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (threshold == null || threshold <= 0) {
                    Toast.makeText(context, "Please enter a valid price threshold", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                onAdd(name, threshold)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
