package com.mcmouse88.choose_color

import android.app.Application
import com.mcmouse88.choose_color.model.colors.InMemoryColorsRepository

class App : Application() {

    val models = listOf<Any>(InMemoryColorsRepository())
}