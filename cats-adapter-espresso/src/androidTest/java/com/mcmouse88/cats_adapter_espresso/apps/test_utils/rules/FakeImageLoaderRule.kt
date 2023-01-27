package com.mcmouse88.cats_adapter_espresso.apps.test_utils.rules

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import coil.Coil
import coil.ImageLoader
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.FakeImageLoader
import org.junit.rules.TestWatcher
import org.junit.runner.Description


/**
 * Класс для подмены реального загрузчика изображений фейковым загрузчиком импорты:
 * ```kotlin
 * import coil.Coil
 * import coil.ImageLoader
 * ```
 * работают почему только на версии не ниже 2.2.0 библиотеки Coil
 */
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