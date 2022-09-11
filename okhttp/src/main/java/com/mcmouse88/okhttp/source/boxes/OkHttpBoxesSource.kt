package com.mcmouse88.okhttp.source.boxes

import com.google.gson.reflect.TypeToken
import com.mcmouse88.okhttp.app.model.boxes.BoxesSource
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.source.base.BaseOkHttpSource
import com.mcmouse88.okhttp.source.base.OkHttpConfig
import com.mcmouse88.okhttp.source.boxes.entities.GetBoxResponseEntity
import com.mcmouse88.okhttp.source.boxes.entities.UpdateBoxRequestEntity
import kotlinx.coroutines.delay
import okhttp3.Request

class OkHttpBoxesSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config), BoxesSource {

    /**
     * Добавляю к запросу объект [TypeToken] мы сообщаем парсеру [Gson], что мы ожидаем в ответе
     * список, где каждый элемент нужно записать в объект класса (в данном случае
     * [GetBoxResponseEntity]).
     */
    override suspend fun getBoxes(boxesFilter: BoxesFilter): List<BoxAndSettings> {
        delay(500)
        val args = if (boxesFilter == BoxesFilter.ONLY_ACTIVE) "?active=true"
        else ""

        val request = Request.Builder()
            .get()
            .endpoint("/boxes$args")
            .build()
        val call = client.newCall(request)
        val typeToken = object : TypeToken<List<GetBoxResponseEntity>>() {}
        val response = call.suspendEnqueue().parseJsonResponse(typeToken)
        return response.map { it.toBoxAndSettings() }
    }

    override suspend fun setIsActive(boxId: Long, isActive: Boolean) {
        val updateBoxRequestEntity = UpdateBoxRequestEntity(isActive)
        val request = Request.Builder()
            .put(updateBoxRequestEntity.toJsonRequestBody())
            .endpoint("/boxes/$boxId")
            .build()
        client.newCall(request).suspendEnqueue()
    }
}