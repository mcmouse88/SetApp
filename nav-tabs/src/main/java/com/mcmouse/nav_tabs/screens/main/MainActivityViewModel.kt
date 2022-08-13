package com.mcmouse.nav_tabs.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username = _username.share()

    init {
        viewModelScope.launch {
            accountsRepository.getAccount().collect {
                if (it == null) _username.value = ""
                else _username.value = "@${it.username}"
            }
        }
    }
}