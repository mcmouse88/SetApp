package com.mcmouse88.file_to_server.data

import androidx.fragment.app.FragmentActivity

interface ActivityRequired {

    fun onActivityCreated(activity: FragmentActivity)

    fun onActivityStarted()

    fun onActivityStopped()

    fun onActivityDestroyed()
}