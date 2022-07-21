package com.mcmouse88.choose_color.views.changecolor

import com.mcmouse88.choose_color.model.colors.NamedColor

data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)