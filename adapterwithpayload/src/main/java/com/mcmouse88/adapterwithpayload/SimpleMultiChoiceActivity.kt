package com.mcmouse88.adapterwithpayload

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.elveum.elementadapter.simpleAdapter
import com.mcmouse88.adapterwithpayload.databinding.ActivitySimpleMultiChoseBinding
import com.mcmouse88.adapterwithpayload.databinding.ItemSelectableBinding

data class SelectableItem(
    val id: Long,
    val name: String,
    var isChecked: Boolean = false
)

class SimpleMultiChoiceActivity : AppCompatActivity() {

    private val items = listOf(
        SelectableItem(1, "Charlie"),
        SelectableItem(2, "Millie"),
        SelectableItem(3, "Lucky"),
        SelectableItem(4, "Poppy"),
        SelectableItem(5, "Oliver"),
        SelectableItem(6, "Sam"),
        SelectableItem(7, "Tiger"),
    )

    private val adapter: SimpleBindingAdapter<SelectableItem> by lazy { createAdapter() }

    private val binding by lazy {
        ActivitySimpleMultiChoseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            rvMultiChoice.layoutManager = LinearLayoutManager(this@SimpleMultiChoiceActivity)
            (rvMultiChoice.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            rvMultiChoice.adapter = adapter
        }
        adapter.submitList(items)
        updateTotalSelected()
    }

    private fun updateTotalSelected() {
        val count = items.count { it.isChecked }
        binding.tvTotalSelected.text = getString(R.string.total_selected, count)
    }

    private fun createAdapter() = simpleAdapter<SelectableItem, ItemSelectableBinding> {
        areContentsSame = { oldItem, newItem -> oldItem == newItem }
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }

        bind { item ->
            tvName.text = item.name
            checkbox.isChecked = item.isChecked
            if (item.isChecked) {
                root.background = ColorDrawable(getColor(R.color.selected_background))
            } else {
                root.background = null
            }
        }

        listeners {
            checkbox.onClick { item ->
                item.isChecked = item.isChecked.not()
                val indexToUpdate = adapter.currentList.indexOfFirst { item.id == it.id }
                adapter.notifyItemChanged(indexToUpdate)
                updateTotalSelected()
            }
        }
    }
}