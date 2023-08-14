package com.mcmouse88.file_to_server.data.files

import com.mcmouse88.file_to_server.domain.files.RemoteFile

interface FilesSource {

    suspend fun getFiles(): List<RemoteFile>

    suspend fun delete(file: RemoteFile)

    suspend fun upload(localUri: String)
}