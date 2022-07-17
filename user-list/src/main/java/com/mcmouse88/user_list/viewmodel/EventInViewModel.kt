package com.mcmouse88.user_list.viewmodel

/**
 * Для того, чтобы совершить какое-то однократное действие (например перейти на другой экран,
 * показать тоаст Toast об ошибке и т.п.) рекомендуется использовать специальные классы
 * эвенты.
 */
class EventInViewModel<T>(
    private val value: T
) {
    private var handler: Boolean = false

    fun getValue(): T? {
        if (handler) return null
        handler = true
        return value
    }
}