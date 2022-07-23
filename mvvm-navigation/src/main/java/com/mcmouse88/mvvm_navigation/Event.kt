package com.mcmouse88.mvvm_navigation

class Event<T>(
    private val value: T
) {
    private var handler: Boolean = false

    fun getValue(): T? {
        if (handler) return null
        handler = !handler
        return value
    }
}