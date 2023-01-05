package com.mcmouse88.foundation.sideeffect.dialogs.plugin

data class DialogConfig(
    val title: String,
    val message: String,
    val positiveButton: String = "",
    val negativeButton: String = "",
    val cancelable: Boolean = true
)