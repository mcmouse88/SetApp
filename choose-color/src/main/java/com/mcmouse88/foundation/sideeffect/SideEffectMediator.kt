package com.mcmouse88.foundation.sideeffect

import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher
import com.mcmouse88.foundation.model.tasks.dispatcher.MainThreadDispatcher
import com.mcmouse88.foundation.utils.ResourceActions

open class SideEffectMediator<Implementation>(
    dispatcher: Dispatcher = MainThreadDispatcher()
) {

    protected val target = ResourceActions<Implementation>(dispatcher)

    fun setTarget(target: Implementation?) {
        this.target.resource = target
    }

    fun clear() {
        target.clear()
    }
}