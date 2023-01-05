package com.mcmouse88.fragment_from_listview

import androidx.fragment.app.Fragment
import com.mcmouse88.fragment_from_listview.model.Cat
import com.mcmouse88.fragment_from_listview.model.CatService

interface AppContract {
    val carsService: CatService

    fun launchCatDetailScreen(cat: Cat)
}

fun Fragment.contract(): AppContract = requireActivity() as AppContract