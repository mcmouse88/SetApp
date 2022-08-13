package com.mcmouse.nav_tabs.screens.main.tabs.settings

import androidx.recyclerview.widget.DiffUtil

class BoxSettingsDiffCallBack(
    private val oldList: List<BoxSetting>,
    private val newList: List<BoxSetting>
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