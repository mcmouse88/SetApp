package com.mcmouse88.file_to_server.data.files.google

import com.mcmouse88.file_to_server.data.files.google.entities.GoogleFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GoogleDriveUploadApi {

    @POST("files?uploadType=multipart")
    suspend fun uploadMultipart(
        @Body multipartBody: MultipartBody
    )

    @POST("files?uploadType=media")
    suspend fun uploadSimple(
        @Header("Content-Length") contentLength: Long,
        @Body fileBody: RequestBody
    ): GoogleFileResponse
}