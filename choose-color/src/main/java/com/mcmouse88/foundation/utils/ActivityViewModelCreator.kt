package com.mcmouse88.foundation.utils

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

typealias ActivityViewModelCreator = () -> ViewModel?

@Suppress("UNCHECKED_CAST")
class ActivityViewModelFactory(
    private val creator: ActivityViewModelCreator
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

inline fun<reified VM : ViewModel> ComponentActivity.activityViewModelCreator(noinline creator: ActivityViewModelCreator): Lazy<VM> {
    return viewModels { ActivityViewModelFactory(creator) }
}