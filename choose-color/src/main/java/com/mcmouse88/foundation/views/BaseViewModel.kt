package com.mcmouse88.foundation.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.utils.Event

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Для удобства работы с результатом создадим три typealias
 */
typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Базовый класс для всех ViewModel (кроме [MainViewModel], который содержив себе опциональный
 * метод [onResult()], на случай если фрагменту нужно получить результат.
 */
open class BaseViewModel : ViewModel() {

    open fun onResult(result: Any) {

    }
}