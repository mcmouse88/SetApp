package com.mcmouse88.readwritefiles

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Event<T>(
    value: T
) {

    private var _value: T? = null
    fun get(): T? = _value.also { _value = null }
}

fun <T> MutableLiveData<T>.share(): LiveData<T> = this

typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>
typealias LiveEvent<T> = LiveData<Event<T>>
typealias EventListener<T> = (T) -> Unit

fun <T> MutableLiveEvent<T>.publishEvent(value: T) {
    this.value = Event(value)
}

fun <T> LiveEvent<T>.observeEvent(owner: LifecycleOwner, listener: EventListener<T>) {
    this.observe(owner) {
        it?.get()?.let { value ->
            listener(value)
        }
    }
}