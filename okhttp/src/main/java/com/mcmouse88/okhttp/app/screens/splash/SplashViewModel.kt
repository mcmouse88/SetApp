package com.mcmouse88.okhttp.app.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.utiils.MutableLiveEvent
import com.mcmouse88.okhttp.app.utiils.publishEvent
import com.mcmouse88.okhttp.app.utiils.share
import kotlinx.coroutines.launch

class SplashViewModel(
    private val accountsRepository: AccountsRepository = Singletons.accountsRepository
) : ViewModel() {

    private val _launchMainScreenEvent = MutableLiveEvent<Boolean>()
    val launchMainScreenEvent = _launchMainScreenEvent.share()

    init {
        viewModelScope.launch {
            _launchMainScreenEvent.publishEvent(accountsRepository.isSignedIn())
        }
    }
}