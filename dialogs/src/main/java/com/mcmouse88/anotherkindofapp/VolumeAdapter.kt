package com.mcmouse88.anotherkindofapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mcmouse88.anotherkindofapp.databinding.ItemVolumeSingleChoiceBinding

class VolumeAdapter(
    private val values: List<Int>
) : BaseAdapter() {

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Int {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        val binding = convertView?.tag as ItemVolumeSingleChoiceBinding? ?:
        ItemVolumeSingleChoiceBinding.inflate(LayoutInflater.from(context))
            .also { it.root.tag = it }

        val volume = getItem(position)
        binding.tvVolumeValueLevelTwo.text = context.getString(R.string.volume_description, volume)
        binding.progressBarVolumeValue.progress = volume

        return binding.root
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}