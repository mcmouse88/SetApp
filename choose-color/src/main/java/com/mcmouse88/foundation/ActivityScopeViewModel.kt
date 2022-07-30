package com.mcmouse88.foundation

import androidx.lifecycle.ViewModel
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.SideEffectMediatorsHolder

class ActivityScopeViewModel : ViewModel() {

    internal val sideEffectMediatorsHolder = SideEffectMediatorsHolder()

    val sideEffectMediators: List<SideEffectMediator<*>>
        get() = sideEffectMediatorsHolder.mediators

    override fun onCleared() {
        sideEffectMediatorsHolder.clear()
        super.onCleared()
    }
}