package com.mcmouse88.mvvm_navigation.navigator

import android.app.Application
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcmouse88.mvvm_navigation.Event
import com.mcmouse88.mvvm_navigation.MainActivity
import com.mcmouse88.mvvm_navigation.R
import com.mcmouse88.mvvm_navigation.screens.base.BaseScreen

const val ARG_SCREEN = "arg_screen"

/**
 * Для того, чтобы управлять навигацией из [ViewModel], создадим класс, который будет
 * наследоваться от [ViewModel], в нашем случае от [AndroidViewModel], так как нам нужен контекст
 * для доступа к android зависимостям
 */
class MainNavigator(
    application: Application
) : AndroidViewModel(application), Navigator {

    /**
     * Добавим поле, которое будет обозначать action
     */
    val whenActivityActive = MainActivityActions()

    /**
     * Для передачи результата создадим следующее поле типа [LiveData], которое будет принимать
     * [Event], так как нам нужно, чтобы событие было выполнено только один раз.
     */
    private val _result = MutableLiveData<Event<Any>>()
    val result: LiveData<Event<Any>>
        get() = _result

    /**
     * Таким образом присвоим методу значение action, и в нем уже будем описывать действия,
     * которые будут выполняться в Активити, тогда, когда эта Активити доступна
     */
    override fun launch(screen: BaseScreen) = whenActivityActive {
        launchFragment(it, screen)
    }


    override fun goBack(result: Any?) = whenActivityActive {
        if (result != null) {
            _result.value = Event(result)
        }
        it.onBackPressed()
    }

    override fun showToast(messageRes: Int) {
        Toast.makeText(getApplication(), messageRes, Toast.LENGTH_SHORT).show()
    }

    override fun getString(messageRes: Int): String {
        return getApplication<Application>().getString(messageRes)
    }

    override fun onCleared() {
        whenActivityActive.clear()
        super.onCleared()
    }

    /**
     *  Для запуска фрагментов, создадим метод [launchFragment()], в котором будет три аргумента,
     *  это Активити, экран, который надо запустить, и нужно ли добавлять фрагмент в backStack.
     *  Получить текущий фрагмент можно используя метод [enclosingClass]
     */
    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack: Boolean = true) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.replace(R.id.fragment_container, fragment).commit()
    }
}