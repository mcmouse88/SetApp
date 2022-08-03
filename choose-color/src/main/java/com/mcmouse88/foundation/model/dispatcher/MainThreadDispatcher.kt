package com.mcmouse88.foundation.model.dispatcher

import android.os.Handler
import android.os.Looper

/**
 * Реализация интерфейса [Dispatcher]
 */
class MainThreadDispatcher : Dispatcher {

    private val handler = Handler(Looper.getMainLooper())

    /**
     * Функция которая будет запускать блок кода в главном потоке(параметр функции), если
     * выполнение данного кода происходит не в главном потоке, ну и выпонять блок кода мгновенно,
     * если находимся в главном потоке. В данном методе будем проверять, если текущий поток
     * является главным потоком, то блок кода запускаем сразу, иначе переводим его выполнение
     * в главный поток при помощи метода [handler.post()]
     */
    override fun dispatch(block: () -> Unit) {
        if (Looper.getMainLooper().thread.id == Thread.currentThread().id) {
            block()
        } else {
            handler.post(block)
        }
    }
}