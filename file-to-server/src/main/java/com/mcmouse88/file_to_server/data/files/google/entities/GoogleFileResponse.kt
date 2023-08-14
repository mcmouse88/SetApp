package com.mcmouse88.file_to_server.data.files.google.entities

import com.mcmouse88.file_to_server.domain.files.RemoteFile

data class GoogleFileResponse(
    val id: String,
    val name: String,
    val size: Long
) {
    fun remoteToFile() = RemoteFile(
        id = id,
        fileName = name,
        size = size
    )
}