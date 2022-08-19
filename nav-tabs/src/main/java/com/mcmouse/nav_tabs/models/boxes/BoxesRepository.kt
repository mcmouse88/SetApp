package com.mcmouse.nav_tabs.models.boxes

import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings
import kotlinx.coroutines.flow.Flow

interface BoxesRepository {

    suspend fun getBoxesAndSettings(onlyActive: Boolean = false): Flow<List<BoxAndSettings>>

    suspend fun activateBox(box: Box)

    suspend fun deactivateBox(box: Box)
}