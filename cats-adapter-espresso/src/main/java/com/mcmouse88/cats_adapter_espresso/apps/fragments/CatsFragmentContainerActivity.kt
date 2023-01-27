package com.mcmouse88.cats_adapter_espresso.apps.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.mcmouse88.catadapterespresso.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatsFragmentContainerActivity : AppCompatActivity(), FragmentRouter {

    private val fragmentCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            fragment: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, fragment, v, savedInstanceState)
            if (fragment is HasTitle) {
                supportActionBar?.title = fragment.title
            } else {
                supportActionBar?.title = getString(R.string.app_name)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragments)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, CatsListFragment())
            }
        }

        updateUi()
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallback, false)
        supportFragmentManager.addOnBackStackChangedListener {
            updateUi()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallback)
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }

    override fun showDetail(catId: Long) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fragment_container, CatDetailsFragment.newInstance(catId))
        }
    }

    private fun updateUi() {
        val showUp = supportFragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    }
}