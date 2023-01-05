package com.mcmouse88.foundation.sideeffect

import com.mcmouse88.foundation.model.dispatcher.Dispatcher
import com.mcmouse88.foundation.model.dispatcher.MainThreadDispatcher
import com.mcmouse88.foundation.utils.ResourceActions


open class SideEffectMediator<Implementation>(
    dispatcher: Dispatcher = MainThreadDispatcher()
) {

    /**
     * Ключевое поле для выполнения задач side effect. В этот target и будет приходить
     * реализация тогда, когда она будет доступна, и соотвественно все команды, которые мы туда
     * передадим будут выполняться в момент когда реализация доступна.
     */
    protected val target = ResourceActions<Implementation>(dispatcher)

    fun setTarget(target: Implementation?) {
        this.target.resource = target
    }

    fun clear() {
        target.clear()
    }
}