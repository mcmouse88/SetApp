package com.mcmouse88.fragment_from_listview

import android.app.Application
import com.mcmouse88.fragment_from_listview.model.CatService

class App : Application() {
    val catService = CatService()
}