package com.mcmouse.nav_tabs.screens.main.tabs.settings

import androidx.recyclerview.widget.DiffUtil
import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings

class BoxSettingsDiffCallBack(
    private val oldList: List<BoxAndSettings>,
    private val newList: List<BoxAndSettings>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].box.id
        val newItem = newList[newItemPosition].box.id
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}