package com.mcmouse88.foundation.model.dispatcher

/**
 * Диспатчер для преобразования таски в корутину, используется в качестве параметра для метода
 * [enqueue()]. Вся ешл оеализация, это получить блок кода, и сразу его выполнить.
 */
class ImmediateDispatcher : Dispatcher {

    override fun dispatch(block: () -> Unit) {
        block()
    }
}