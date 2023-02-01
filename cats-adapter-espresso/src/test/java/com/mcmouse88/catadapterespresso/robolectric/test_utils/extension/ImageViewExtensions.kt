package com.mcmouse88.catadapterespresso.robolectric.test_utils.extension

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader.FakeImageLoaderDrawable
import org.robolectric.Shadows

/**
 * [Shadows] это классы библиотеки [Robolectric], которые подменяют классы android SDK, чтобы
 * просимулировать работу реального устройства без самого устройства
 */
fun ImageView.containsDrawable(@DrawableRes res: Int, @ColorRes tintColorRes: Int? = null): Boolean {
    if (containsTint(tintColorRes).not()) return false
    return Shadows.shadowOf(drawable).createdFromResId == res
}

fun ImageView.containsDrawable(other: Drawable, @ColorRes tintColorRes: Int? = null): Boolean {
    if (containsTint(tintColorRes).not()) return false
    val imageDrawable = drawable
    return if (other is FakeImageLoaderDrawable) {
        imageDrawable is FakeImageLoaderDrawable && imageDrawable.url == other.url
    } else {
        other == imageDrawable
    }
}

private fun ImageView.containsTint(@ColorRes tintColorRes: Int?): Boolean {
    return if (tintColorRes != null) {
        val imageTintList = this.imageTintList
        imageTintList != null && imageTintList.defaultColor == ContextCompat.getColor(context, tintColorRes)
    } else {
        this.imageTintList == null
    }
}