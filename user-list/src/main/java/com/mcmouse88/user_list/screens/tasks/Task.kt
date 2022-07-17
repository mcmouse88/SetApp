package com.mcmouse88.user_list.screens.tasks

typealias CallBack<T> = (T) -> Unit

interface Task<T> {

    fun onSuccess(callBack: CallBack<T>): Task<T>

    fun onError(callBack: CallBack<Throwable>): Task<T>

    fun cancel()

    fun await(): T
}