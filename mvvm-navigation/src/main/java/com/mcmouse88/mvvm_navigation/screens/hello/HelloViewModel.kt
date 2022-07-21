package com.mcmouse88.mvvm_navigation.screens.hello

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcmouse88.mvvm_navigation.R
import com.mcmouse88.mvvm_navigation.navigator.Navigator
import com.mcmouse88.mvvm_navigation.screens.base.BaseViewModel
import com.mcmouse88.mvvm_navigation.screens.edit.EditFragment

class HelloViewModel(
    private val navigator: Navigator,
    screen: HelloFragment.Screen
) : BaseViewModel() {

    private val _currentMessage = MutableLiveData<String>()
    val currentMessage: LiveData<String>
        get() = _currentMessage

    init {
        _currentMessage.value = navigator.getString(R.string.hello_world)
    }

    override fun onResult(result: Any) {
        if (result is String) _currentMessage.value = result
    }

    fun onEditPressed() {
        navigator.launch(EditFragment.Screen(initialValue = currentMessage.value ?: ""))
    }
}