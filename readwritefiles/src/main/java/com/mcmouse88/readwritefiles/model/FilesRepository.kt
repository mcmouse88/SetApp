package com.mcmouse88.readwritefiles.model

interface FilesRepository {

    suspend fun openFile(): ReadableFile

    suspend fun saveFile(suggestedName: String): WriteableFile
}