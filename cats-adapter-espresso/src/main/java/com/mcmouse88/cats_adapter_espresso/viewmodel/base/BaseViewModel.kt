package com.mcmouse88.cats_adapter_espresso.viewmodel.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    protected fun<T> LiveData<T>.update(value: T) {
        (this as MutableLiveData<T>).value = value
    }

    protected fun<T> liveData(value: T? = null): LiveData<T> {
        return if (value == null) {
            MutableLiveData()
        } else {
            MutableLiveData(value)
        }
    }
}