package com.mcmouse88.okhttp.source_retrofit.boxes

import com.mcmouse88.okhttp.app.model.boxes.BoxesSource
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.source_retrofit.boxes.entity.GetBoxResponseEntity
import com.mcmouse88.okhttp.source_retrofit.boxes.entity.UpdateBoxRequestEntity
import com.mcmouse88.okhttp.source_retrofit.base.BaseRetrofitSource
import com.mcmouse88.okhttp.source_retrofit.base.RetrofitConfig
import kotlinx.coroutines.delay

class RetrofitBoxesSource(
    config: RetrofitConfig
) : BaseRetrofitSource(config), BoxesSource {

    private val boxesApi = retrofit.create(BoxesApi::class.java)

    override suspend fun getBoxes(boxesFilter: BoxesFilter): List<BoxAndSettings> = wrapRetrofitException {
        delay(500)
        val isActive: Boolean? = if (boxesFilter == BoxesFilter.ONLY_ACTIVE) true
        else null
        boxesApi.getBoxes(isActive)
            .map(GetBoxResponseEntity::toBoxAndSettings)
    }

    override suspend fun setIsActive(boxId: Long, isActive: Boolean) = wrapRetrofitException {
        val updateBoxRequestEntity = UpdateBoxRequestEntity(
            isActive = isActive
        )
        boxesApi.setIsActive(boxId, updateBoxRequestEntity)
    }
}