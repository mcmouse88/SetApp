package com.mcmouse.nav_tabs.screens.main.admin

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.databinding.ItemTreeElementBinding

class AdminItemsAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<AdminItemsAdapter.AdminTreeHolder>(), View.OnClickListener {

    private var items: List<AdminTreeItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminTreeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTreeElementBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return AdminTreeHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminTreeHolder, position: Int) {
        val item = items[position]
        holder.itemView.tag = item
        with(holder.binding) {
            tvAttributes.text = prepareAttributesText(item)
            ivExpandCollapseIndicator.setImageResource(getExpansionIcon(item))
            adjustStartOffset(item, tvAttributes)
            root.isClickable = item.expansionStatus != ExpansionStatus.NOT_EXPANDABLE
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onClick(view: View) {
        val item = view.tag as AdminTreeItem
        listener.onItemToggled(item)
    }

    fun renderItems(items: List<AdminTreeItem>) {
        val diffCallback = AdminTreeItemDiffCallback(this.items, items)
        val result = DiffUtil.calculateDiff(diffCallback)
        this.items = items
        result.dispatchUpdatesTo(this)
    }

    private fun prepareAttributesText(item: AdminTreeItem): CharSequence {
        val attributesText = item.attributes.entries.joinToString("<br>") {
            if (it.value.isBlank()) "<b>${it.key}</b>: ${it.value}"
            else "<b>${it.key}</b>"
        }
        return Html.fromHtml(attributesText, Html.FROM_HTML_MODE_LEGACY)
    }

    @DrawableRes
    private fun getExpansionIcon(item: AdminTreeItem): Int {
        return when(item.expansionStatus) {
            ExpansionStatus.EXPANDED -> R.drawable.ic_minus
            ExpansionStatus.COLLAPSED -> R.drawable.ic_plus
            else -> R.drawable.ic_dot
        }
    }

    private fun adjustStartOffset(item: AdminTreeItem, attributesTextView: TextView) {
        val context = attributesTextView.context
        val spacePerLevel = context.resources.getDimensionPixelSize((R.dimen.tree_level_size))
        val totalSpace = (item.level + 1) * spacePerLevel

        val layoutParams = attributesTextView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = totalSpace
        attributesTextView.layoutParams = layoutParams
    }

    inner class AdminTreeHolder(
        val binding: ItemTreeElementBinding
        ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        fun onItemToggled(item: AdminTreeItem)
    }
}