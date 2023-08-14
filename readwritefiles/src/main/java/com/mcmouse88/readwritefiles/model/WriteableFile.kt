package com.mcmouse88.readwritefiles.model

interface WriteableFile {
    suspend fun write(data: String)
}