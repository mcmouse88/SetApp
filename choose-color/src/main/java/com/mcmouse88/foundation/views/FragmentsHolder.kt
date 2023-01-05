package com.mcmouse88.foundation.views

import com.mcmouse88.foundation.ActivityScopeViewModel

interface FragmentsHolder {
    fun notifyScreenUpdate()

    fun getActivityScopeViewModel(): ActivityScopeViewModel
}