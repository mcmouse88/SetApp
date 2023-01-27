package com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

/**
 * Кроме своих матчером также можно и создавать свои action, здесь он нужен для того, чтобы
 * нажимать на определенную view внутри списка.
 */
class ClickOnViewAction(private val id: Int? = null) : ViewAction {

    override fun getDescription(): String {
        return "click on item view"
    }

    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isDisplayingAtLeast(90)
    }

    override fun perform(uiController: UiController?, view: View) {
        if (id == null) view.performClick()
        else view.findViewById<View>(id)?.performClick()
    }

    companion object {
        fun clickOnView(@IdRes id: Int? = null): ViewAction = ClickOnViewAction(id)
    }
}