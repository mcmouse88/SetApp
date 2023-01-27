package com.mcmouse88.cats_adapter_espresso

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Если в проекте используется Hilt нужно написать дополнительный класс, который наследуется от
 * [AndroidJUnitRunner] и в нем переопределить метод [newApplication], в котором нужно
 * указать специальный тип библиотеки hilt [HiltTestApplication], и далее нужно его указать в
 * gradle файле в defaultConfig в качестве [testInstrumentationRunner]
 */
class HiltAndroidTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}