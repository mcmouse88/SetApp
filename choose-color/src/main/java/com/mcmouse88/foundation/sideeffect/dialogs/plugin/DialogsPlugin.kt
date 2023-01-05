package com.mcmouse88.foundation.sideeffect.dialogs.plugin

import android.content.Context
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.SideEffectPlugin

class DialogsPlugin : SideEffectPlugin<DialogsSideEffectMediator, DialogsSideEffectImpl> {

    override val mediatorClass: Class<DialogsSideEffectMediator>
        get() = DialogsSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<DialogsSideEffectImpl> {
       return DialogsSideEffectMediator()
    }

    override fun createImplementation(mediator: DialogsSideEffectMediator): DialogsSideEffectImpl? {
        return DialogsSideEffectImpl(mediator.retainedState)
    }
}