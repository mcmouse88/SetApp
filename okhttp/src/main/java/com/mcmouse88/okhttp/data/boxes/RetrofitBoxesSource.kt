package com.mcmouse88.okhttp.data.boxes

import com.mcmouse88.okhttp.data.base.BaseRetrofitSource
import com.mcmouse88.okhttp.data.base.RetrofitConfig
import com.mcmouse88.okhttp.data.boxes.entity.GetBoxResponseEntity
import com.mcmouse88.okhttp.data.boxes.entity.UpdateBoxRequestEntity
import com.mcmouse88.okhttp.domain.boxes.BoxesSource
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitBoxesSource @Inject constructor(
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