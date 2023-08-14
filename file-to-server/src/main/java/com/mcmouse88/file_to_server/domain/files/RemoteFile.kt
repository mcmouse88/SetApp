package com.mcmouse88.file_to_server.domain.files

data class RemoteFile(
    val id: String,
    val fileName: String,
    val size: Long
)