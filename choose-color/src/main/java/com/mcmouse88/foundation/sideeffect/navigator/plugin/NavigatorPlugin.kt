package com.mcmouse88.foundation.sideeffect.navigator.plugin

import android.content.Context
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.SideEffectPlugin
import com.mcmouse88.foundation.sideeffect.navigator.Navigator

class NavigatorPlugin(
    private val navigator: Navigator
) : SideEffectPlugin<Navigator, Navigator> {

    override val mediatorClass: Class<Navigator>
        get() = Navigator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Navigator> {
        return NavigatorSideEffectMediator()
    }

    override fun createImplementation(mediator: Navigator): Navigator? {
        return navigator
    }
}