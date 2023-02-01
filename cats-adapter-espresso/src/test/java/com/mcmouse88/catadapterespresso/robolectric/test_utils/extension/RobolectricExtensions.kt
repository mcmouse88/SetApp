package com.mcmouse88.catadapterespresso.robolectric.test_utils.extension

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import org.robolectric.android.controller.ActivityController

fun <T : Activity> ActivityController<T>.require(): T {
    return requireNotNull(get())
}

fun <T : Activity> ActivityScenario<T>.with(block: T.() -> Unit) {
    onActivity(block)
}
