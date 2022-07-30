package com.mcmouse88.foundation.sideeffect

class SideEffectPluginsManager {

    private val _plugins = mutableListOf<SideEffectPlugin<*, *>>()
    val plugins: List<SideEffectPlugin<*, *>>
        get() = _plugins

    fun<Mediator, Implementation> register(plugin: SideEffectPlugin<Mediator, Implementation>) {
        _plugins.add(plugin)
    }
}