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
import com.mcmouse88.foundation.views.BaseFragment
import com.mcmouse88.foundation.views.HasScreenTitle

class MainActivity : AppCompatActivity() {

    /**
     * Сама [MainActivity] содержит ссылку на [ViewModel], предназначенную для работы с активити,
     * которая реализует интерфейс [Navigator] и [UiActions]
     */
    private val activityViewModel by viewModels<ActivityScopeViewModel> { AndroidViewModelFactory(application) }

    private val fragmentCallBacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            notifyScreenUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            activityViewModel.launchFragment(
                activity =  this,
                screen = CurrentColorFragment.Screen(),
                addToBackStack = false
            )
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallBacks, false)
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallBacks)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        activityViewModel.whenActivityActive.resource = this
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.whenActivityActive.resource = null
    }

    /**
     *  Отвечает за корректное отображение toolBar с кнопкой назад и названием текущего экрана, а
     *  также частично за возвращение результата обратно на экран, если таковой имеется. Для того,
     *  чтобы заголовок toolBar динамически менялся при выборе цвета, мы в данном методе проверяем
     *  реализует ли текущий фрагмент интерфейс [HasScreenTitle] и не равен ли заголовок null.
     *  Также в каждом фрагменте есть метод [notifyScreenUpdates()], который и сообщает Активити,
     *  что нужно перерисовать ActionBar, который находится вверху.
     */
    fun notifyScreenUpdates() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        if (fragment is HasScreenTitle && fragment.getScreenTitle() != null) {
            supportActionBar?.title = fragment.getScreenTitle()
        } else {
            supportActionBar?.title = getString(R.string.app_name)
        }

        val result = activityViewModel.result.value?.getValue() ?: return

        if (fragment is BaseFragment) fragment.viewModel.onResult(result)
    }
}