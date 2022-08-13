package com.mcmouse.nav_tabs.models.boxes.entities

import androidx.annotation.StringRes

data class Box(
    val id: Int,
    @StringRes val colorNameRes: Int,
    val colorValue: Int
)