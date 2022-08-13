package com.mcmouse.nav_tabs.screens.main.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.AuthException
import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.Field
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.utils.MutableUnitLiveEvent
import com.mcmouse.nav_tabs.utils.publishEvent
import com.mcmouse.nav_tabs.utils.requireValue
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.launch

class SignInViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state = _state.share()

    private val _clearPasswordEvent = MutableUnitLiveEvent()
    val clearPasswordEvent = _clearPasswordEvent.share()

    private val _showAuthErrorToastEvent = MutableUnitLiveEvent()
    val showAuthErrorToastEvent = _showAuthErrorToastEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    fun signIn(email: String, password: String) = viewModelScope.launch {
        showProgress()
        try {
            accountsRepository.signIn(email, password)
            launchTabsScreen()
        } catch (e: EmptyFieldException) {
            processEmptyFieldException(e)
        } catch (e: AuthException) {
            processAuthException()
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyEmailError = e.field == Field.Email,
            emptyPasswordError = e.field == Field.Password,
            signInInProgress = false
        )
    }

    private fun processAuthException() {
        _state.value = _state.requireValue().copy(
            signInInProgress = false
        )
        clearPasswordField()
        showAuthErrorToast()
    }

    private fun showProgress() {
        _state.value = State(signInInProgress = true)
    }

    private fun clearPasswordField() = _clearPasswordEvent.publishEvent()

    private fun showAuthErrorToast() = _showAuthErrorToastEvent.publishEvent()

    private fun launchTabsScreen() = _navigateToTabsEvent.publishEvent()

    data class State(
        val emptyEmailError: Boolean = false,
        val emptyPasswordError: Boolean = false,
        val signInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signInInProgress
        val enableViews: Boolean get() = !signInInProgress
    }
}