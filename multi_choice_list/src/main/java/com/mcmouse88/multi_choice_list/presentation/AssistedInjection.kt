package com.mcmouse88.multi_choice_list.presentation

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AssistedViewModelFactory<VM : ViewModel>(
    private val producer: () -> VM
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return producer() as T
    }
}

inline fun<reified VM : ViewModel> Fragment.assistedViewModel(
    noinline producer: () -> VM
) : Lazy<VM> {
    return viewModels { AssistedViewModelFactory(producer) }
}


inline fun<reified VM : ViewModel> ComponentActivity.assistedViewModel(
    noinline producer: () -> VM
) : Lazy<VM> {
    return viewModels { AssistedViewModelFactory(producer) }
}

