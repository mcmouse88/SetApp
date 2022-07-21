package com.mcmouse88.choose_color.utils

class Event<T>(
    private val value: T
) {

    private var handled: Boolean = false

    fun getValue(): T? {
        if (handled) return null
        handled = !handled
        return value
    }
}