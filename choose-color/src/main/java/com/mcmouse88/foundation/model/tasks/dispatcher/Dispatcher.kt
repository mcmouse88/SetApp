package com.mcmouse88.foundation.model.tasks.dispatcher

interface Dispatcher {

    fun dispatch(block: () -> Unit)
}