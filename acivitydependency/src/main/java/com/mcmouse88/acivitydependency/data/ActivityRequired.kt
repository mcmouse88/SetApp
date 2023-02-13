package com.mcmouse88.acivitydependency.data

import androidx.fragment.app.FragmentActivity

/**
 * Интерфейс который связывает модель(репозиторий) с жизненным циклом активитиб
 * также возможно использование [LifecycleOwner]
 */
interface ActivityRequired {

    fun onActivityCreated(activity: FragmentActivity)

    fun onActivityStarted()

    fun onActivityStopped()

    fun onActivityDestroyed()
}