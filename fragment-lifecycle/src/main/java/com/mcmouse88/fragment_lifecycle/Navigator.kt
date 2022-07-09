package com.mcmouse88.fragment_lifecycle

import androidx.fragment.app.Fragment

interface Navigator {
    fun launchNext()

    fun generateUUID(): String

    fun update()
}

fun Fragment.navigator(): Navigator = requireActivity() as Navigator