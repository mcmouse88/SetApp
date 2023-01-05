package com.mcmouse.nav_tabs.screens.main.tabs.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings

class SettingsAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<SettingsAdapter.SettingsHolder>(), View.OnClickListener {

    private var settings: List<BoxAndSettings> = emptyList()

    override fun onClick(view: View?) {
        val checkBox = view as CheckBox
        val box = view.tag as Box
        if (checkBox.isChecked) {
            listener.enableBox(box)
        } else {
            listener.disableBox(box)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val checkBox = inflater.inflate(R.layout.item_settings, parent, false) as CheckBox
        checkBox.setOnClickListener(this)
        return SettingsHolder(checkBox)
    }

    override fun onBindViewHolder(holder: SettingsHolder, position: Int) {
        val setting = settings[position]
        val context = holder.itemView.context
        holder.checkBox.tag = setting.box

        if (holder.checkBox.isChecked != setting.isActive) {
            holder.checkBox.isChecked = setting.isActive
        }

        val colorName = setting.box.colorName
        holder.checkBox.text = context.getString(R.string.enable_checkbox, colorName)
    }

    override fun getItemCount() = settings.size

    fun renderSettings(settings: List<BoxAndSettings>) {
        val diffResult = DiffUtil.calculateDiff(BoxSettingsDiffCallBack(this.settings, settings))
        this.settings = settings
        diffResult.dispatchUpdatesTo(this)
    }

    inner class SettingsHolder(val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)

    interface Listener {
        fun enableBox(box: Box)
        fun disableBox(box: Box)
    }
}