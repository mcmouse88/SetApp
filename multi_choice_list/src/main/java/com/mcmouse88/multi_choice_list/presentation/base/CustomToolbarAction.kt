package com.mcmouse88.multi_choice_list.presentation.base

interface CustomToolbarAction {

    val action: ToolbarAction?

    fun onNewUpdater(updater: ToolbarUpdater)
}