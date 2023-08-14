package com.mcmouse88.file_to_server.data.chooser

interface FileChooser {
    suspend fun chooseFile(): String
}