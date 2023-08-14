package com.mcmouse88.file_to_server.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.file_to_server.domain.AuthException
import com.mcmouse88.file_to_server.domain.accounts.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mcmouse88.file_to_server.domain.Result
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.sample

@OptIn(FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _navigateBackToSignInScreenEvent = MutableUnitLiveEvent()
    val navigateBackToSignInScreenEvent = _navigateBackToSignInScreenEvent.share()

    init {
        viewModelScope.launch {
            accountRepository.getAccount()
                .filter { it is Result.Error && it.exception is AuthException }
                .sample(500L)
                .collectLatest {
                    navigateBackToSignInScreen()
                }
        }
    }

    private fun navigateBackToSignInScreen() {
        _navigateBackToSignInScreenEvent.publishEvent()
    }
}