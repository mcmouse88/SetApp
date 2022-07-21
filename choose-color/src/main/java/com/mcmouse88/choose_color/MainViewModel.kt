package com.mcmouse88.choose_color

import android.app.Application
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.mcmouse88.choose_color.utils.Event
import com.mcmouse88.choose_color.utils.ResourceActions
import com.mcmouse88.choose_color.views.Navigator
import com.mcmouse88.choose_color.views.UiActions
import com.mcmouse88.choose_color.views.base.BaseScreen
import com.mcmouse88.choose_color.views.base.LiveEvent
import com.mcmouse88.choose_color.views.base.MutableLiveEvent

const val ARG_SCREEN = "arg_screen"

class MainViewModel(
    application: Application
) : AndroidViewModel(application), Navigator, UiActions {

    val whenActivityActive = ResourceActions<MainActivity>()

    private val _result = MutableLiveEvent<Any>()
    val result: LiveEvent<Any>
        get() = _result

    override fun launch(screen: BaseScreen) = whenActivityActive {
        launchFragment(it, screen)
    }

    override fun goBack(result: Any?) = whenActivityActive {
        if (result != null) {
            _result.value = Event(result)
        }
        it.onBackPressed()
    }

    override fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    override fun getString(messageRes: Int, vararg args: Any): String {
        return getApplication<App>().getString(messageRes, *args)
    }

    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack: Boolean = true) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment

        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.setCustomAnimations(
            R.anim.enter,
            R.anim.exit,
            R.anim.pop_enter,
            R.anim.pop_exit
        )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCleared() {
        whenActivityActive.clear()
        super.onCleared()
    }
}