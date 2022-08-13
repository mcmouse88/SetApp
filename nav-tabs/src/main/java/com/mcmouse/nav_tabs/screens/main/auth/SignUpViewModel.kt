package com.mcmouse.nav_tabs.screens.main.auth

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.models.AccountAlreadyExistException
import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.Field
import com.mcmouse.nav_tabs.models.PasswordMismatchException
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import com.mcmouse.nav_tabs.utils.MutableUnitLiveEvent
import com.mcmouse.nav_tabs.utils.publishEvent
import com.mcmouse.nav_tabs.utils.requireValue
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _showSuccessSignUpMessageEvent = MutableUnitLiveEvent()
    val showSuccessSignUpMessageEvent = _showSuccessSignUpMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    private val _state = MutableLiveData(State())
    val state = _state.share()

    fun signUp(signUpData: SignUpData) {
        viewModelScope.launch {
            showProgress()
            try {
                accountsRepository.signUp(signUpData)
                showSuccessSignUpMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: PasswordMismatchException) {
                processPasswordMismatchException()
            } catch (e: AccountAlreadyExistException) {
                processAccountAlreadyExistException()
            } finally {
                hideProgress()
            }
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = when(e.field) {
            Field.Email -> _state.requireValue()
                .copy(emailErrorMessageRes = R.string.field_is_empty)
            Field.Username -> _state.requireValue()
                .copy(usernameErrorMessageRes = R.string.field_is_empty)
            Field.Password -> _state.requireValue()
                .copy(passwordErrorMessageRes = R.string.field_is_empty)
            else -> throw IllegalStateException("Unknown field")
        }
    }

    private fun processPasswordMismatchException() {
        _state.value = _state.requireValue()
            .copy(repeatPasswordMessageRes = R.string.password_mismatch)
    }

    private fun processAccountAlreadyExistException() {
        _state.value = _state.requireValue()
            .copy(emailErrorMessageRes = R.string.account_already_exists)
    }

    private fun showProgress() {
        _state.value = State(signupInProgress = true)
    }

    private fun hideProgress() {
        _state.value = _state.requireValue().copy(signupInProgress = false)
    }

    private fun showSuccessSignUpMessage() = _showSuccessSignUpMessageEvent.publishEvent()

    private fun goBack() = _goBackEvent.publishEvent()

    data class State(
        @StringRes val emailErrorMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val passwordErrorMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val repeatPasswordMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val usernameErrorMessageRes: Int = NO_ERROR_MESSAGE,
        val signupInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signupInProgress
        val enableViews: Boolean get() = !signupInProgress
    }

    companion object {
        const val NO_ERROR_MESSAGE = 0
    }
}