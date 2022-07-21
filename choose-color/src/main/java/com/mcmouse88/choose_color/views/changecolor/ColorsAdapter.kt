package com.mcmouse88.choose_color.views.changecolor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mcmouse88.choose_color.databinding.ItemColorBinding
import com.mcmouse88.choose_color.model.colors.NamedColor

class ColorsAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<ColorsAdapter.ColorHolder>(), View.OnClickListener {

    var items: List<NamedColorListItem> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onClick(view: View) {
        val item = view.tag as NamedColor
        listener.onColorChosen(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemColorBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return ColorHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        val namedColor = items[position].namedColor
        val selected = items[position].selected
        with(holder.binding) {
            root.tag = namedColor
            tvColorName.text = namedColor.name
            chooseColorView.setBackgroundColor(namedColor.value)
            ivSelectedIndicator.visibility = if (selected) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = items.size

    inner class ColorHolder(val binding: ItemColorBinding) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        fun onColorChosen(namedColor: NamedColor)
    }
}