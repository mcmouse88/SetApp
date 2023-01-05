package com.mcmouse88.okhttp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.utils.MutableLiveEvent
import com.mcmouse88.okhttp.utils.publishEvent
import com.mcmouse88.okhttp.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _launchMainScreenEvent = MutableLiveEvent<Boolean>()
    val launchMainScreenEvent = _launchMainScreenEvent.share()

    init {
        viewModelScope.launch {
            _launchMainScreenEvent.publishEvent(accountsRepository.isSignedIn())
        }
    }
}