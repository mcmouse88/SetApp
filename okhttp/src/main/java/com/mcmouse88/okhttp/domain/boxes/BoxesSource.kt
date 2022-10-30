package com.mcmouse88.okhttp.domain.boxes

import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter

interface BoxesSource {

    suspend fun getBoxes(boxesFilter: BoxesFilter): List<BoxAndSettings>

    suspend fun setIsActive(boxId: Long, isActive: Boolean)
}