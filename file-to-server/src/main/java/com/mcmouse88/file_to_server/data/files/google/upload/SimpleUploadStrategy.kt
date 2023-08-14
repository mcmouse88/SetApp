package com.mcmouse88.file_to_server.data.files.google.upload

import com.mcmouse88.file_to_server.data.files.google.GoogleDriveApi
import com.mcmouse88.file_to_server.data.files.google.GoogleDriveUploadApi
import com.mcmouse88.file_to_server.data.files.google.entities.MetaDataRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpleUploadStrategy @Inject constructor(
    private val googleDriveUploadApi: GoogleDriveUploadApi,
    private val googleDriveApi: GoogleDriveApi
) : UploadStrategy {

    override suspend fun upload(readFileResult: ReadFileResult) {
        val bytes = readFileResult.bytes
        val fileName = readFileResult.fileName

        val response = googleDriveUploadApi.uploadSimple(
            contentLength = bytes.size.toLong(),
            fileBody = bytes.toRequestBody()
        )
        googleDriveApi.updateMetaData(
            fileId = response.id,
            metaDataRequestBody = MetaDataRequestBody(name = fileName)
        )
    }
}