package com.mcmouse88.multi_choice_list.presentation.base

import androidx.annotation.DrawableRes

class ToolbarAction(
    @DrawableRes val iconRes: Int,
    val action: () -> Unit
)