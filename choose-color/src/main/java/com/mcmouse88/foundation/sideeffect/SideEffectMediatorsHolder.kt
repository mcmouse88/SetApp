package com.mcmouse88.foundation.sideeffect

import android.app.Application
import android.content.Context

@Suppress("UNCHECKED_CAST")
class SideEffectMediatorsHolder {

    private val _mediators = mutableMapOf<Class<*>, SideEffectMediator<*>>()
    val mediators: List<SideEffectMediator<*>>
        get() = _mediators.values.toList()

    fun<T> contains(clazz: Class<T>): Boolean {
        return _mediators.contains(clazz)
    }

    fun<Mediator, Implementation> putWithPlugin(
        applicationContext: Context,
        plugin: SideEffectPlugin<Mediator, Implementation>
    ) {
        _mediators[plugin.mediatorClass] = plugin.createMediator(applicationContext)
    }

    fun<Mediator, Implementation> setTargetWithPlugin(
        plugin: SideEffectPlugin<Mediator, Implementation>,
        sideEffectImplementationsHolder: SideEffectImplementationsHolder
    ) {
        val interMediateViewService = get(plugin.mediatorClass)
        val target = sideEffectImplementationsHolder.getWithPlugin(plugin)
        if (interMediateViewService is SideEffectMediator<*>) {
            (interMediateViewService as SideEffectMediator<Implementation>).setTarget(target)
        }
    }

    fun<T> get(clazz: Class<T>): T {
        return _mediators[clazz] as T
    }

    fun removeTargets() {
        _mediators.values.forEach { it.setTarget(null) }
    }

    fun clear() {
        _mediators.values.forEach { it.clear() }
        _mediators.clear()
    }
}