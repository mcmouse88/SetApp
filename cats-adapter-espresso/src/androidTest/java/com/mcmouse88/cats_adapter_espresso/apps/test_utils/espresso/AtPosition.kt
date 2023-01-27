package com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.contrib.RecyclerViewActions
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Расширение для тестирования позиций [RecyclerView]
 */
class AtPosition(
    private val position: Int,
    private val matcher: Matcher<View>
) : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("recyclerView with the specified ID and at position: $position")
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is RecyclerView) return false
        val viewHolder = item.findViewHolderForAdapterPosition(position) ?: return false
        return matcher.matches(viewHolder.itemView)
    }

    companion object {
        fun scrollToPosition(position: Int): ViewAction {
            return RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position)
        }

        fun actionOnItemAtPosition(position: Int, action: ViewAction): ViewAction {
            return RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                action
            )
        }

        fun atPosition(
            position: Int,
            matcher: Matcher<View>
        ): Matcher<View> {
            return AtPosition(position, matcher)
        }
    }
}