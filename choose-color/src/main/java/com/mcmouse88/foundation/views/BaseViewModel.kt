package com.mcmouse88.foundation.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcmouse88.foundation.utils.Event

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Базовый класс для всех ViewModel (кроме [MainViewModel], который содержив себе опциональный
 * метод [onResult()], на случай если фрагменту нужно получить результат.
 */
open class BaseViewModel : ViewModel() {

    open fun onResult(result: Any) {

    }
}