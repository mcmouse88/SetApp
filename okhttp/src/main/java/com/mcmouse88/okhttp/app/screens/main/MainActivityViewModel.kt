package com.mcmouse88.okhttp.app.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.utiils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val username = _userName.share()

    init {
        viewModelScope.launch {
            accountsRepository.getAccount().collect { result ->
                _userName.value = result.getValueOrNull()?.username?.let { "@$it" } ?: ""
            }
        }
    }
}