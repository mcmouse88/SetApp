package com.mcmouse88.fragment_lifecycle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import com.mcmouse88.fragment_lifecycle.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), Navigator {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val fragmentListener: FragmentLifecycleCallbacks =
        object : FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                update()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, createFragment())
                .commit()
        }
    }

    override fun onDestroy() {
        _binding = null
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        super.onDestroy()
    }

    override fun launchNext() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, createFragment())
            .commit()
    }

    override fun generateUUID() = UUID.randomUUID().toString()

    override fun update() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        binding.tvCurrentFragmentUuid.text = if (currentFragment is HasUUID) {
            currentFragment.getUUID()
        } else {
            ""
        }
        if (currentFragment is NumberListener) {
            currentFragment.onNewScreenNumber(1 + supportFragmentManager.backStackEntryCount)
        }
    }

    private fun createFragment(): RandomFragment {
        return RandomFragment.newInstance(generateUUID())
    }
}