package com.mcmouse.nav_tabs.models.boxes.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class SettingsTuple(
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

/**
 * для того чтобы объединять данные из таблиц используется аннотация [Embedded]
 */
data class BoxAndSettingAndTuple(
    @Embedded val boxDbEntity: BoxDbEntity,
    @Embedded val settingDbEntity: AccountBoxSettingDbEntity?
)