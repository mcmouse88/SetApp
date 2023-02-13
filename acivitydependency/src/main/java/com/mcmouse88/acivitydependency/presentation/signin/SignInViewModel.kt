package com.mcmouse88.acivitydependency.presentation.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.acivitydependency.domain.AuthException
import com.mcmouse88.acivitydependency.domain.ErrorHandler
import com.mcmouse88.acivitydependency.domain.Result
import com.mcmouse88.acivitydependency.domain.accounts.Account
import com.mcmouse88.acivitydependency.domain.accounts.AccountsRepository
import com.mcmouse88.acivitydependency.domain.launchIn
import com.mcmouse88.acivitydependency.presentation.MutableUnitLiveEvent
import com.mcmouse88.acivitydependency.presentation.publishEvent
import com.mcmouse88.acivitydependency.presentation.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>(State.Loading)
    val stateLiveData = _stateLiveData.share()

    private val _navigateToFileEvent = MutableUnitLiveEvent()
    val navigateToProfileEvent = _navigateToFileEvent.share()

    fun load() {
        errorHandler.launchIn(viewModelScope) {
            accountsRepository.reloadAccount()
        }

        viewModelScope.launch {
            accountsRepository.getAccount()
                .onEach { _stateLiveData.value = it.toState() }
                .takeWhile { it !is Result.Success }
                .collect()
            navigateToProfile()
        }
    }

    fun signIn() {
        errorHandler.launchIn(viewModelScope) {
            accountsRepository.oauthSignIn()
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
                if (exception is AuthException) {
                    State.NotLoggedIn
                } else {
                    State.Error(errorHandler.getErrorMessage(exception))
                }
            }
        }
    }

    sealed interface State {
        object Loading : State
        object NotLoggedIn : State
        class Error(val message: String) : State
    }
}