package com.mcmouse88.readwritefiles

import androidx.fragment.app.FragmentActivity

interface ActivityRequired {

    fun onActivityCreated(activity: FragmentActivity)

    fun onActivityStarted()

    fun onActivityStopped()

    fun onActivityDestroyed(isFinishing: Boolean)
}