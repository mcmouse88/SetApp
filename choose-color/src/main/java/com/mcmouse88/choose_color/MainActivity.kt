package com.mcmouse88.choose_color

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.mcmouse88.choose_color.views.currentcolor.CurrentColorFragment
import com.mcmouse88.foundation.ActivityScopeViewModel
import com.mcmouse88.foundation.navigator.IntermediateNavigator
import com.mcmouse88.foundation.navigator.StackFragmentNavigator
import com.mcmouse88.foundation.uiactions.AndroidUiActions
import com.mcmouse88.foundation.utils.activityViewModelCreator
import com.mcmouse88.foundation.views.BaseFragment
import com.mcmouse88.foundation.views.FragmentsHolder
import com.mcmouse88.foundation.views.HasScreenTitle

class MainActivity : AppCompatActivity(), FragmentsHolder {

    private val activityViewModel by activityViewModelCreator<ActivityScopeViewModel> {
        ActivityScopeViewModel(
            uiActions =  AndroidUiActions(applicationContext),
            navigator =  IntermediateNavigator()
        )
    }

    private lateinit var navigator: StackFragmentNavigator

    override fun notifyScreenUpdate() {
        navigator.notifyScreenUpdates()
    }

    override fun getActivityScopeViewModel(): ActivityScopeViewModel {
        return activityViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator = StackFragmentNavigator(
            activity =  this,
            containerId = R.id.fragment_container,
            defaultTitle = getString(R.string.app_name),
            animations = StackFragmentNavigator.Animations(
                enterAnim = R.anim.enter,
                exitAnim = R.anim.exit,
                popEnterAnim = R.anim.pop_enter,
                popExitAnim = R.anim.pop_exit
            )
        ) {
            CurrentColorFragment.Screen()
        }
        navigator.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        navigator.onDestroy()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        activityViewModel.navigator.setTarget(navigator)
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.navigator.setTarget(null)
    }
}