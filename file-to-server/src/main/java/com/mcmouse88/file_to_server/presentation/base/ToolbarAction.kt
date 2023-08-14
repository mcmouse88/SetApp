package com.mcmouse88.file_to_server.presentation.base

import androidx.annotation.DrawableRes

class ToolbarAction(
    @DrawableRes val iconRes: Int,
    val action: () -> Unit
)