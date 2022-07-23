package com.mcmouse88.mvvm_navigation.screens.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcmouse88.mvvm_navigation.Event
import com.mcmouse88.mvvm_navigation.R
import com.mcmouse88.mvvm_navigation.navigator.Navigator
import com.mcmouse88.mvvm_navigation.screens.base.BaseViewModel

class EditViewModel(
    private val navigator: Navigator,
    screen: EditFragment.Screen
) : BaseViewModel() {

    /**
     * Создадим событие, которое будет принимать аргументы для экрана, при инициализации, получим
     * значение, которое пришло нам с другого фрагмента, и установим уже во фрагменте значение
     * в EditText
     */
    private val _initialMessageEvent = MutableLiveData<Event<String>>()
    val initialMessageEvent: LiveData<Event<String>>
        get() = _initialMessageEvent

    init {
        _initialMessageEvent.value = Event(screen.initialValue)
    }

    fun onSavePressed(message: String) {
        if (message.isBlank()) {
            navigator.showToast(R.string.empty_message)
            return
        }
        navigator.goBack(message)
    }

    fun onCancelPressed() {
        navigator.goBack()
    }
}