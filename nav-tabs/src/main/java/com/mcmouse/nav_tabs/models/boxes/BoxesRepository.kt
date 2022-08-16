package com.mcmouse.nav_tabs.models.boxes

import com.mcmouse.nav_tabs.models.boxes.entities.Box
import kotlinx.coroutines.flow.Flow

interface BoxesRepository {

    suspend fun getBoxes(onlyActive: Boolean = false): Flow<List<Box>>

    suspend fun activateBox(box: Box)

    suspend fun deactivateBox(box: Box)
}