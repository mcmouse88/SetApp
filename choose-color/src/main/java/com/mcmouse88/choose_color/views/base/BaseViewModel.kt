package com.mcmouse88.choose_color.views.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcmouse88.choose_color.utils.Event

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

abstract class BaseViewModel : ViewModel() {

    open fun onResult(result: Any) {

    }
}