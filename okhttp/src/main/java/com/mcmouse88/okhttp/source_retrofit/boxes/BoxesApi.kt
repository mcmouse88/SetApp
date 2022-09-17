package com.mcmouse88.okhttp.source_retrofit.boxes

import com.mcmouse88.okhttp.source_retrofit.boxes.entity.GetBoxResponseEntity
import com.mcmouse88.okhttp.source_retrofit.boxes.entity.UpdateBoxRequestEntity
import retrofit2.http.*

interface BoxesApi {

    @PUT("boxes/{boxId}")
    suspend fun setIsActive(
        @Path("boxId") boxId: Long,
        @Body updateBoxRequestEntity: UpdateBoxRequestEntity
    )

    /**
     * Чтобы добавить аргумент к строке запроса используется аннотация [Query]
     */
    @GET("boxes")
    suspend fun getBoxes(
        @Query("active") isActive: Boolean?
    ): List<GetBoxResponseEntity>
}