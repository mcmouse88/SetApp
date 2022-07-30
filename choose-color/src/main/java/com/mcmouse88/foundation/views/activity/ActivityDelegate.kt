@file:Suppress("DEPRECATION")

package com.mcmouse88.foundation.views.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mcmouse88.foundation.ActivityScopeViewModel
import com.mcmouse88.foundation.sideeffect.SideEffectImplementationsHolder
import com.mcmouse88.foundation.sideeffect.SideEffectPlugin
import com.mcmouse88.foundation.sideeffect.SideEffectPluginsManager
import com.mcmouse88.foundation.utils.activityViewModelCreator

class ActivityDelegate(
    private val activity: AppCompatActivity
) : LifecycleObserver {

    internal val sideEffectPluginsManager = SideEffectPluginsManager()

    private val activityViewModel by activity.activityViewModelCreator<ActivityScopeViewModel> {
        ActivityScopeViewModel()
    }

    private val implementerHolder = SideEffectImplementationsHolder()

    init {
        activity.lifecycle.addObserver(this)
    }

    fun onBackPressed(): Boolean {
        return implementerHolder.implementation.any { it.onBackPressed() }
    }

    fun onSupportNavigateUp(): Boolean? {
        for (service in implementerHolder.implementation) {
            val value = service.onSupportNavigateUp()
            if (value != null) return value
        }
        return null
    }

    fun onCreate(savedInstanceState: Bundle?) {
        sideEffectPluginsManager.plugins.forEach { plugin ->
            setupSideEffectMediator(plugin)
            setupSideEffectImplementer(plugin)
        }
        implementerHolder.implementation.forEach { it.onCreate(savedInstanceState) }
    }

    fun onSavedInstanceState(outState: Bundle) {
        implementerHolder.implementation.forEach { it.onSaveInstanceState(outState) }
    }

    fun onActivityResult(requestCode: Int, responseCode: Int, data: Intent?) {
        implementerHolder.implementation.forEach { it.onActivityResult(requestCode, responseCode, data) }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        granted: IntArray
    ) {
        implementerHolder.implementation.forEach { it.onRequestPermissionsResult(
            requestCode,
            permissions,
            granted
        ) }
    }

    fun notifyScreenUpdates() {
        implementerHolder.implementation.forEach { it.onRequestUpdates() }
    }

    fun getActivityScopeViewModel(): ActivityScopeViewModel {
        return activityViewModel
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        sideEffectPluginsManager.plugins.forEach {
            activityViewModel.sideEffectMediatorsHolder.setTargetWithPlugin(it, implementerHolder)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        activityViewModel.sideEffectMediatorsHolder.removeTargets()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        implementerHolder.clear()
    }

    private fun setupSideEffectMediator(plugin: SideEffectPlugin<*, *>) {
        val holder = activityViewModel.sideEffectMediatorsHolder

        if (!holder.contains(plugin.mediatorClass)) {
            holder.putWithPlugin(activity.applicationContext, plugin)
        }
    }

    private fun setupSideEffectImplementer(plugin: SideEffectPlugin<*, *>) {
        implementerHolder.putWithPlugin(
            plugin,
            activityViewModel.sideEffectMediatorsHolder,
            activity
        )
    }
}