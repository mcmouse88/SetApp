package com.mcmouse88.file_to_server.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mcmouse88.file_to_server.R
import com.mcmouse88.file_to_server.data.ActivityRequired
import com.mcmouse88.file_to_server.databinding.ActivityMainBinding
import com.mcmouse88.file_to_server.presentation.base.CustomToolbarAction
import com.mcmouse88.file_to_server.presentation.base.ToolbarUpdater
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ToolbarUpdater {

    @Inject
    lateinit var activityRequiredStaff: Set<@JvmSuppressWildcards ActivityRequired>

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = requireNotNull(_binding) { "ActivityMainBinding is null" }

    private var currentFragment: Fragment? = null

    private val viewModel by viewModels<MainViewModel>()

    private var navController: NavController? = null

    private val fragmentCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is NavHostFragment) return
            currentFragment = f
            (f as? CustomToolbarAction)?.onNewUpdater(this@MainActivity)
            renderToolbar()
        }
    }

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = destination.label
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        initNavController()
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallback, true)
        navController?.addOnDestinationChangedListener(destinationListener)
        activityRequiredStaff.forEach { it.onActivityCreated(this) }

        viewModel.navigateBackToSignInScreenEvent.observeEvent(this) {
            if (navController?.currentDestination?.id != R.id.signInFragment) {
                navController?.navigate(R.id.action_logout)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activityRequiredStaff.forEach { it.onActivityStarted() }
    }

    override fun onStop() {
        super.onStop()
        activityRequiredStaff.forEach { it.onActivityStopped() }
    }

    override fun onDestroy() {
        _binding = null
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallback)
        navController?.removeOnDestinationChangedListener(destinationListener)
        activityRequiredStaff.forEach { it.onActivityDestroyed() }
        super.onDestroy()
    }

    override fun notifyChanges() {
        renderToolbar()
    }

    private fun initNavController() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
    }

    private fun renderToolbar() = with(binding) {
        val fragment = currentFragment ?: return@with
        val action = (fragment as? CustomToolbarAction)?.action
        if (action != null) {
            ivToolbarAction.apply {
                isVisible = true
                setImageResource(action.iconRes)
                setOnClickListener { action.action() }
            }
        } else ivToolbarAction.isVisible = false
    }
}