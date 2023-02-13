package com.mcmouse88.acivitydependency.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.mcmouse88.acivitydependency.R
import com.mcmouse88.acivitydependency.data.ActivityRequired
import com.mcmouse88.acivitydependency.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**
     * Инжект коллекции элементов реализующих интерфейс [ActivityRequired], Dagger и Hilt
     * генерируют логику внедрения зависмостей на языке Java и котлиновские дженерики превращаются
     * в типы с масками то есть в котлин это:
     * ```kotlin
     * Set<ActivityRequired>
     * ```
     * а в java это:
     * ```java
     * Set<? extends ActivityRequired>
     * ```
     * и аннотация [JvmSuppressWildcards] как раз и говорит что нужно брать именно котлиновский
     * дженерик, то есть явный тип.
     */
    @Inject
    lateinit var activityRequiredStaff: Set<@JvmSuppressWildcards ActivityRequired>

    private val viewModel by viewModels<MainViewModel>()

    private val navController by lazy {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navHost.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityRequiredStaff.forEach {
            it.onActivityCreated(this)
        }

        viewModel.navigateBackToSignInScreenEvent.observeEvent(this) {
            if (navController.currentDestination?.id != R.id.signInFragment) {
                navController.navigate(R.id.action_logout)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activityRequiredStaff.forEach {
            it.onActivityStarted()
        }
    }

    override fun onStop() {
        super.onStop()
        activityRequiredStaff.forEach {
            it.onActivityStopped()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRequiredStaff.forEach {
            it.onActivityDestroyed()
        }
    }
}