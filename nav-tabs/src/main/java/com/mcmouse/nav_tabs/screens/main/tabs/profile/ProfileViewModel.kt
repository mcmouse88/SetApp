package com.mcmouse.nav_tabs.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.utils.MutableLiveEvent
import com.mcmouse.nav_tabs.utils.publishEvent
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _account = MutableLiveData<Account>()
    val account = _account.share()

    private val _restartFromLoginEvent = MutableLiveEvent<Unit>()
    val restartFromLoginEvent = _restartFromLoginEvent.share()

    init {
        viewModelScope.launch {
            accountsRepository.getAccount().collect {
                _account.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            accountsRepository.logout()
            restartAppFromLoginScreen()
        }
    }

    private fun restartAppFromLoginScreen() {
        _restartFromLoginEvent.publishEvent()
    }
}