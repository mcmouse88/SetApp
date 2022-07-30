package com.mcmouse88.foundation.sideeffect

import androidx.appcompat.app.AppCompatActivity

@Suppress("UNCHECKED_CAST")
class SideEffectImplementationsHolder {

    private val _implementation = mutableMapOf<Class<*>, Any>()
    val implementation: Collection<SideEffectImplementation>
        get() = _implementation.values.filterIsInstance <SideEffectImplementation>()

    fun<Mediator, Implementation> getWithPlugin(plugin: SideEffectPlugin<Mediator, Implementation>): Implementation? {
        return _implementation[plugin.mediatorClass] as Implementation
    }

    fun<Mediator, Implementation> putWithPlugin(
        plugin: SideEffectPlugin<Mediator, Implementation>,
        sideEffectMediatorsHolder: SideEffectMediatorsHolder,
        activity: AppCompatActivity
    ) {
        val sideEffectMediators = sideEffectMediatorsHolder.get(plugin.mediatorClass)
        val target = plugin.createImplementation(sideEffectMediators)
        if (target != null && target is SideEffectImplementation) {
            _implementation[plugin.mediatorClass] = target
            target.injectActivity(activity)
        }
    }

    fun clear() {
        _implementation.clear()
    }
}