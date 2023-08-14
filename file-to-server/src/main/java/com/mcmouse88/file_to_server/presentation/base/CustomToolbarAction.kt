package com.mcmouse88.file_to_server.presentation.base

interface CustomToolbarAction {

    val action: ToolbarAction?

    fun onNewUpdater(updater: ToolbarUpdater)
}