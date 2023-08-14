package com.mcmouse88.readwritefiles.model

interface ReadableFile {
    suspend fun read(): String
}