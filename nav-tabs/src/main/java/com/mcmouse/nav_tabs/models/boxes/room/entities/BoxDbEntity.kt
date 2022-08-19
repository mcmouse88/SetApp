package com.mcmouse.nav_tabs.models.boxes.room.entities

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mcmouse.nav_tabs.models.boxes.entities.Box


@Entity(tableName = "boxes")
data class BoxDbEntity(
    @[PrimaryKey ColumnInfo(name = "box_id")] val boxId: Long,
    @ColumnInfo(name = "color_name") val colorName: String,
    @ColumnInfo(name = "color_value") val colorValue: String
) {
    fun toBox(): Box = Box(
        id = boxId,
        colorName = colorName,
        colorValue = Color.parseColor(colorValue)
    )
}