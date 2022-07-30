package com.mcmouse88.foundation.sideeffect.permissions.plugin

import android.content.Context
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.SideEffectPlugin

class PermissionsPlugin : SideEffectPlugin<PermissionsSideEffectMediator, PermissionsSideEffectImpl> {

    override val mediatorClass: Class<PermissionsSideEffectMediator>
        get() = PermissionsSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<PermissionsSideEffectImpl> {
        return PermissionsSideEffectMediator(applicationContext)
    }

    override fun createImplementation(mediator: PermissionsSideEffectMediator): PermissionsSideEffectImpl {
        return PermissionsSideEffectImpl(mediator.retainedState)
    }
}