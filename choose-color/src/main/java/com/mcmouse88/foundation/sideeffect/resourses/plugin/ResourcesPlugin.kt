package com.mcmouse88.foundation.sideeffect.resourses.plugin

import android.content.Context
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.SideEffectPlugin

class ResourcesPlugin : SideEffectPlugin<ResourcesSideEffectMediator, Nothing> {

    override val mediatorClass: Class<ResourcesSideEffectMediator>
        get() = ResourcesSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return ResourcesSideEffectMediator(applicationContext)
    }
}