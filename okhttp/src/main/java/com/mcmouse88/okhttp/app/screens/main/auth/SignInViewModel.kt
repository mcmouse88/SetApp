package com.mcmouse88.okhttp.app.screens.main.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.EmptyFieldException
import com.mcmouse88.okhttp.app.model.Field
import com.mcmouse88.okhttp.app.model.InvalidCredentialsException
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.screens.base.BaseViewModel
import com.mcmouse88.okhttp.app.utiils.*
import com.mcmouse88.okhttp.app.utiils.logger.LogcatLogger
import com.mcmouse88.okhttp.app.utiils.logger.Logger

class SignInViewModel(
    private val accountsRepository: AccountsRepository = Singletons.accountsRepository,
    private val logger: Logger = LogcatLogger
) : BaseViewModel(accountsRepository, logger) {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _clearPasswordEvent = MutableUnitLiveEvent()
    val clearPasswordEvent = _clearPasswordEvent.share()

    private val _showAuthErrorToastEvent = MutableLiveEvent<Int>()
    val showAuthErrorToastEvent = _showAuthErrorToastEvent.share()

    private val _navigateToTabsEvent = MutableUnitLiveEvent()
    val navigateToTabsEvent = _navigateToTabsEvent.share()

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyEmailError = e.field == Field.Email,
            emptyPasswordError = e.field == Field.Password
        )
    }

    fun signIn(email: String, password: String) = viewModelScope.safeLaunch {
        showProgress()
        try {
            accountsRepository.signIn(email, password)
            launchTabsScreen()
        } catch (e: EmptyFieldException) {
            processEmptyFieldException(e)
        } catch (e: InvalidCredentialsException) {
            processInvalidCredentialsException()
        } finally {
            hideProgress()
        }
    }

    private fun processInvalidCredentialsException() {
        clearPasswordField()
        showAuthErrorToast()
    }

    private fun showProgress() {
        _state.value = _state.requireValue().copy(signInInProgress = true)
    }

    private fun hideProgress() {
        _state.value = _state.requireValue().copy(signInInProgress = false)
    }

    private fun clearPasswordField() = _clearPasswordEvent.publishEvent()

    private fun showAuthErrorToast() = _showAuthErrorToastEvent.publishEvent(R.string.invalid_email_or_password)

    private fun launchTabsScreen() = _navigateToTabsEvent.publishEvent()

    data class State(
        val emptyEmailError: Boolean = false,
        val emptyPasswordError: Boolean = false,
        val signInInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signInInProgress
        val enableViews: Boolean get() = signInInProgress.not()
    }
}