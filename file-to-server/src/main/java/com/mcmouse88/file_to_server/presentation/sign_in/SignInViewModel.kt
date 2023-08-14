package com.mcmouse88.file_to_server.presentation.sign_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.file_to_server.domain.AuthException
import com.mcmouse88.file_to_server.domain.ErrorHandler
import com.mcmouse88.file_to_server.domain.Result
import com.mcmouse88.file_to_server.domain.accounts.Account
import com.mcmouse88.file_to_server.domain.accounts.AccountRepository
import com.mcmouse88.file_to_server.domain.launchIn
import com.mcmouse88.file_to_server.presentation.MutableUnitLiveEvent
import com.mcmouse88.file_to_server.presentation.publishEvent
import com.mcmouse88.file_to_server.presentation.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>(State.Loading)
    val stateLiveData = _stateLiveData.share()

    private val _navigateToFileEvent = MutableUnitLiveEvent()
    val navigateToProfileEvent = _navigateToFileEvent.share()

    init {
        load()
    }

    fun load() {
        errorHandler.launchIn(viewModelScope) {
            accountRepository.reloadAccount()
        }
        viewModelScope.launch {
            accountRepository.getAccount()
                .onEach { _stateLiveData.value = it.toState() }
                .takeWhile { it !is Result.Success }
                .collect()
            navigateToProfile()
        }
    }

    fun signIn() {
        errorHandler.launchIn(viewModelScope) {
            accountRepository.oauthSignIn()
            navigateToProfile()
        }
    }

    private fun navigateToProfile() {
        _navigateToFileEvent.publishEvent()
    }

    private fun Result<Account>.toState(): State {
        return when (this) {
            is Result.Pending, is Result.Success -> State.Loading
            is Result.Error -> {
                if (exception is AuthException) State.NotLoggedIn
                else State.Error(errorHandler.getErrorMessage(exception))
            }
        }
    }

    sealed interface State {
        object Loading : State
        object NotLoggedIn : State
        class Error(val message: String) : State
    }
}