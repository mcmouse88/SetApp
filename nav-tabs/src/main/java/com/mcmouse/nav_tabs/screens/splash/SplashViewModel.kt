package com.mcmouse.nav_tabs.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.utils.MutableLiveEvent
import com.mcmouse.nav_tabs.utils.publishEvent
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.launch

class SplashViewModel(private val accountsRepository: AccountsRepository) : ViewModel() {

    private val _launchMainScreenEvent = MutableLiveEvent<Boolean>()
    val launchMainScreenEvent = _launchMainScreenEvent.share()

    init {
        viewModelScope.launch {
            _launchMainScreenEvent.publishEvent(accountsRepository.isSignedIn())
        }
    }
}