package com.mcmouse88.foundation.model.dispatcher

interface Dispatcher {

    fun dispatch(block: () -> Unit)
}