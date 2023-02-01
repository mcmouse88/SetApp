package com.mcmouse88.catadapterespresso.robolectric.test_utils.rules

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import coil.Coil
import coil.ImageLoader
import com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader.FakeImageLoader
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class FakeImageLoaderRule : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Coil.setImageLoader(FakeImageLoader())
    }

    override fun finished(description: Description) {
        super.finished(description)
        val defaultLoader = ImageLoader(ApplicationProvider.getApplicationContext<Application>())
        Coil.setImageLoader(defaultLoader)
    }
}