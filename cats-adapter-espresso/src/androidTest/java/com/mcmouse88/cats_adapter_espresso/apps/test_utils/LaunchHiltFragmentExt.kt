package com.mcmouse88.cats_adapter_espresso.apps.test_utils

import android.content.ComponentName
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.fragments.TestFragmentActivity

/**
 * Данный метод написан для запуска фрагментов при использовании библиотеки Hilt. В данном методе
 * осуществляется запустк тестовой активити, которая помечена аннотацией [AndroidEntryPoint], и
 * после запуска активити в нее добавляется фрагмент, чтобы тестовая активити не попала в релизную
 * сборку, она добавлена в отдельный каталог с пометкой debug, и также там нужно создать еще XML
 * файл манифеста.
 */
inline fun<reified T : Fragment> launchHiltFragment(
    noinline creator: (() -> T)? = null
): AutoCloseable {
    val intent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            TestFragmentActivity::class.java
        )
    )

    return ActivityScenario.launch<TestFragmentActivity>(intent).onActivity {
        val fragment = creator?.invoke() ?: it.supportFragmentManager.fragmentFactory.instantiate(
            requireNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        it.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment)
            .commitNow()
    }
}