package com.mcmouse88.file_to_server.data.files.google.upload

interface UploadStrategy {
    suspend fun upload(readFileResult: ReadFileResult)
}