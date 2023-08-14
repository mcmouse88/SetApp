package com.mcmouse88.file_to_server.data.files.google.upload

import com.google.gson.Gson
import com.mcmouse88.file_to_server.data.files.google.GoogleDriveUploadApi
import com.mcmouse88.file_to_server.data.files.google.entities.MetaDataRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultipartUploadStrategy @Inject constructor(
    private val googleDriveUploadApi: GoogleDriveUploadApi,
    private val gson: Gson
) : UploadStrategy {

    override suspend fun upload(readFileResult: ReadFileResult) {
        val bytes = readFileResult.bytes
        val fileName = readFileResult.fileName

        val metaData = MetaDataRequestBody(name = fileName)
        val metadataJson = gson.toJson(metaData)

        val metaDataRequestBody = metadataJson.toRequestBody("application/json".toMediaType())
        val contentRequestBody = bytes.toRequestBody()

        val fullMultipartRequestBody = MultipartBody.Builder()
            .addPart(metaDataRequestBody)
            .addPart(contentRequestBody)
            .build()
        googleDriveUploadApi.uploadMultipart(fullMultipartRequestBody)
    }
}