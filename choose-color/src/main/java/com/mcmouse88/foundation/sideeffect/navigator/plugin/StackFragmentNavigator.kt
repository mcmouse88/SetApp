@file:Suppress("DEPRECATION")

package com.mcmouse88.foundation.sideeffect.navigator.plugin

import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mcmouse88.foundation.sideeffect.SideEffectImplementation
import com.mcmouse88.foundation.sideeffect.navigator.Navigator
import com.mcmouse88.foundation.utils.Event
import com.mcmouse88.foundation.views.BaseFragment
import com.mcmouse88.foundation.views.BaseScreen
import com.mcmouse88.foundation.views.BaseScreen.Companion.ARG_SCREEN
import com.mcmouse88.foundation.views.HasScreenTitle

class StackFragmentNavigator(
    @IdRes private val containerId: Int,
    private val defaultTitle: String,
    private val animations: Animations,
    private val initialScreen: () -> BaseScreen
) : SideEffectImplementation(), Navigator, LifecycleObserver {

    private var result: Event<Any>? = null

    private val fragmentCallBacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            view: View,
            savedInstanceState: Bundle?
        ) {
            onRequestUpdates()
            publishResults(fragment)
        }
    }

    override fun launch(screen: BaseScreen) {
        launchFragment(screen)
    }

    override fun goBack(result: Any?) {
        if (result != null) {
            this.result = Event(result)
        }
        requireActivity().onBackPressed()
    }

    /**
     * [onCreate()] и [onDestroy()] методы жизненного цикла [StackFragmentNavigator], так он
     * работает на стороне Активити
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            launchFragment(
                screen = initialScreen(),
                addToBackStack = false
            )
        }
        requireActivity().supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallBacks, false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        requireActivity().supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallBacks)
    }

    override fun onBackPressed(): Boolean {
        val fragment = getCurrentFragment()
        return if (fragment is BaseFragment) fragment.viewModel.onBackPressed() else false
    }

    override fun onSupportNavigateUp(): Boolean {
        requireActivity().onBackPressed()
        return true
    }

    override fun onRequestUpdates() {
        val fragment = getCurrentFragment()

        if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
            requireActivity().supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            requireActivity().supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        if (fragment is HasScreenTitle && fragment.getScreenTitle() != null) {
            requireActivity().supportActionBar?.title = fragment.getScreenTitle()
        } else {
            requireActivity().supportActionBar?.title = defaultTitle
        }
    }

    private fun launchFragment(screen: BaseScreen, addToBackStack: Boolean = true) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment

        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.setCustomAnimations(
            animations.enterAnim,
            animations.exitAnim,
            animations.popEnterAnim,
            animations.popExitAnim
        )
            .replace(containerId, fragment)
            .commit()
    }

    private fun publishResults(fragment: Fragment) {
        val result = result?.getValue() ?: return
        if (fragment is BaseFragment) fragment.viewModel.onResult(result)
    }

    private fun getCurrentFragment(): Fragment? = requireActivity().supportFragmentManager.findFragmentById(containerId)

    class Animations(
        @AnimRes val enterAnim: Int,
        @AnimRes val exitAnim: Int,
        @AnimRes val popEnterAnim: Int,
        @AnimRes val popExitAnim: Int
    )
}