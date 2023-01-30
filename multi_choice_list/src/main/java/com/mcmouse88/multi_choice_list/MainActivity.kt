package com.mcmouse88.multi_choice_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.mcmouse88.multi_choice_list.databinding.ActivityMainBinding
import com.mcmouse88.multi_choice_list.presentation.base.CustomToolbarAction
import com.mcmouse88.multi_choice_list.presentation.base.ToolbarUpdater
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ToolbarUpdater {

    private val navController: NavController
        get() {
            val fragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
            return fragment.navController
        }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var currentFragment: Fragment? = null

    private val fragmentCallback = object : FragmentLifecycleCallbacks() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallback, true)
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallback)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun notifyChanges() {
        renderToolbar()
    }

    private fun renderToolbar() {
        val fragment = currentFragment ?: return
        val action = (fragment as? CustomToolbarAction)?.action
        if (action != null) {
            binding.toolbarActionImageView.isVisible = true
            binding.toolbarActionImageView.setImageResource(action.iconRes)
            binding.toolbarActionImageView.setOnClickListener { action.action() }
        } else {
            binding.toolbarActionImageView.isVisible = false
        }
    }
}