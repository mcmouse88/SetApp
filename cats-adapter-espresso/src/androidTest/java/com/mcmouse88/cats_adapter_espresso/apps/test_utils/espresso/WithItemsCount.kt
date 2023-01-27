package com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Еще одно расширение библиотеки, которая проверяет соответствие количества элементов в
 * [RecyclerView]
 */
class WithItemsCount(
    private val count: Int
) : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("recyclerView with item count = $count")
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is RecyclerView) return false
        val adapter = item.adapter ?: return false
        return adapter.itemCount == count
    }

    companion object {
        fun withItemsCount(count: Int): Matcher<View> = WithItemsCount(count)
    }
}