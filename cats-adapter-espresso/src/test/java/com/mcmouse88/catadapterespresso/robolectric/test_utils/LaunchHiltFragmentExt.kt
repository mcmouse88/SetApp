package com.mcmouse88.catadapterespresso.robolectric.test_utils

import android.content.ComponentName
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.fragments.TestFragmentActivity

inline fun <reified T : Fragment> launchHiltFragment(
    noinline creator: (() -> T)? = null
): ActivityScenario<*> {
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
