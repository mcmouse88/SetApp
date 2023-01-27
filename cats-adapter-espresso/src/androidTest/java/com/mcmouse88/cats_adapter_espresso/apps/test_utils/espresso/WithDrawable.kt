package com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Также для библиотеки Espresso можно писать свои расширения, в этом случае нужно наследоваться
 * от класса [TypeSafeMatcher] и реализовать два метода [describeTo] - нужен для описания матчераЮ
 * что он делает, это нужно, чтобы при запуске теста было понятно что пошло не так, вывод
 * прописанный в этом методе потом будет в логах и [matchesSafely] - проверяет, что view, которая
 * попадает в качестве аргумента в этот метод соответствует необходимым нам требованиям (в данном
 * примере проверяется соответствие картинки по пикселям через bitmap и метод [sameAs])
 */
class WithDrawable(
    private val drawable: Drawable,
    @ColorRes private val tintColorRes: Int? = null
) : TypeSafeMatcher<View>() {

    constructor(
        @DrawableRes drawableRes: Int,
        @ColorRes tintColorRes: Int?
    ) : this(
        requireNotNull(
            ContextCompat.getDrawable(
                ApplicationProvider.getApplicationContext(),
                drawableRes
            )
        ),
        tintColorRes
    )

    override fun describeTo(description: Description) {
        description.appendText("ImageView with the specified drawable res.")
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is ImageView) return false
        if (tintColorRes != null) {
            drawable.setTintList(
                ColorStateList.valueOf(
                    ContextCompat.getColor(item.context, tintColorRes)
                )
            )
        }
        return item.drawable.toBitmap().sameAs(drawable.toBitmap())
    }

    companion object {
        fun withDrawable(
            drawable: Drawable,
            @ColorRes tintColorRes: Int? = null
        ): Matcher<View> = WithDrawable(drawable, tintColorRes)

        fun withDrawable(
            @DrawableRes drawableRes: Int,
            @ColorRes tintColorRes: Int? = null
        ): Matcher<View> = WithDrawable(drawableRes, tintColorRes)
    }
}