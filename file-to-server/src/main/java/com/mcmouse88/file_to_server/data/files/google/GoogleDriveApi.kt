package com.mcmouse88.file_to_server.data.files.google

import com.mcmouse88.file_to_server.data.files.google.entities.GooglePageResponse
import com.mcmouse88.file_to_server.data.files.google.entities.MetaDataRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleDriveApi {

    @GET("files")
    suspend fun listFiles(
        @Query("pageToken") pageToken: String?,
        @Query("fields") fields: String = "files(id,name,size)"
    ): GooglePageResponse

    @DELETE("files/{fileId}")
    suspend fun delete(
        @Path("fileId") fileId: String
    ): Response<Unit>

    @PATCH("files/{fileId}")
    suspend fun updateMetaData(
        @Path("fileId") fileId: String,
        @Body metaDataRequestBody: MetaDataRequestBody
    )
}