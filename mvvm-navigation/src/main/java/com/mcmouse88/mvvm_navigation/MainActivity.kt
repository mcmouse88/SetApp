package com.mcmouse88.mvvm_navigation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.mcmouse88.mvvm_navigation.navigator.MainNavigator
import com.mcmouse88.mvvm_navigation.screens.base.BaseFragment
import com.mcmouse88.mvvm_navigation.screens.hello.HelloFragment

class MainActivity : AppCompatActivity() {

    private val navigator by viewModels<MainNavigator> { AndroidViewModelFactory(application) }

    /**
     * В данном свойстве будем прослушиать фрагменты, в котором переопределим метод
     * [onFragmentViewCreated()]. В нем проверим, что если в backStack имеются фрагменты, то
     * мы показываем кнопку назад в toolBar, если же нет, то не показываем. Так как во фрагмент
     * может приходить результат, то пробуем его получить, если результата нету, то просто выходим
     * из метода. А также проверим, что если текущий фрагмент это [BaseFragment], то во [ViewModel]
     * данного фрагмента передаем результат.
     */
    private val fragmentCallBacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            view: View,
            savedInstanceState: Bundle?
        ) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            val result = navigator.result.value?.getValue() ?: return
            if (fragment is BaseFragment) {
                fragment.viewModel.onResult(result)
            }
        }
    }

    /**
     * В методах [onCreate] регистрируем наш fragmentCallBacks, а в методе [onDestroy]
     * отписываемся от него.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigator.launchFragment(this, HelloFragment.Screen(), false)
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallBacks, false)
    }

    /**
     * Данный метод предназначен для обработки нажатия стрелки на toolBar
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Для того, чтобы определить когда Активити активно, а когда нет определим это поведение в
     * методах [onResume()] и [onPause()]
     */
    override fun onResume() {
        super.onResume()
        navigator.whenActivityActive.mainActivity = this
    }

    override fun onPause() {
        super.onPause()
        navigator.whenActivityActive.mainActivity = null
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallBacks)
        super.onDestroy()
    }
}