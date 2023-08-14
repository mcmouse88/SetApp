package com.mcmouse88.file_to_server.data.files.google.entities

data class GooglePageResponse(
    val nextPageToken: String?,
    val files: List<GoogleFileResponse>
)