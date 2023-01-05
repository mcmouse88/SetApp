package com.mcmouse88.foundation.utils

import com.mcmouse88.foundation.model.dispatcher.Dispatcher

typealias ResourceAction<T> = (T) -> Unit

class ResourceActions<T>(
    private val dispatcher: Dispatcher
) {

    /**
     * Переменная для ресурса (в нашем случае ресурсом будет выступать [MainActivity], когда
     * ресурс не null, то действия будут выпоняться мгновенно. Метод [set] для данного свойства
     * работает следующим образом: если новое значение которое мы устанавливаем для свойства
     * не равно null, то проверяем есть ли у нас какие-либо действия в списке, которые нужно
     * выполнить, а по окончанию выполнения очищать список действий.
     */
    var resource: T? = null
    set (newValue) {
        field = newValue
        if (newValue != null) {
            actions.forEach { action ->
                dispatcher.dispatch {
                    action(newValue)
                }
                action(newValue)
            }
            actions.clear()
        }
    }

    private val actions = mutableListOf<ResourceAction<T>>()

    /**
     * для удобства работы с этим классом переопределим оператор [invoke()], в котором будем
     * проверять что если ресурс равен null, то нужно добавить действия в список на выполнение,
     * иначе выпонить действие. Активен ресурс или нет мы определяем в самой [MainActivity], в
     * методе [onResume()] устанавливаем значение ресурса (то есть присваиваем ресурсу текущее
     * значение [MainActivity], а в методе [onPause()] присваиваем ресурсу значение null
     */
    operator fun invoke(action: ResourceAction<T>) {
        val resource = this.resource
        if (resource == null) {
            actions += action
        } else {
            dispatcher.dispatch {
                action(resource)
            }
        }
    }

    /**
     * Метод очищающий список действий, на случай если они нам больше не нужны, например если
     * [MainViewModel] уже не существует (у нас он будет вызываться в методе [onCleared()]
     * в [MainViewModel]
     */
    fun clear() {
        actions.clear()
    }
}