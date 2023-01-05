package com.mcmouse88.okhttp.presentation.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.domain.EmptyFieldException
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.presentation.base.BaseViewModel
import com.mcmouse88.okhttp.utils.MutableLiveEvent
import com.mcmouse88.okhttp.utils.MutableUnitLiveEvent
import com.mcmouse88.okhttp.utils.logger.Logger
import com.mcmouse88.okhttp.utils.publishEvent
import com.mcmouse88.okhttp.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountsRepository, logger) {

    private val _initialUserNameEvent = MutableLiveEvent<String>()
    val initialUserNameEvent = _initialUserNameEvent.share()

    private val _saveInProgress = MutableLiveData(false)
    val saveInProgress = _saveInProgress.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    private val _showErrorEvent = MutableLiveEvent<Int>()
    val showErrorEvent = _showErrorEvent.share()

    init {
        viewModelScope.launch {
            val res = accountsRepository.getAccount()
                .filter { it.isFinished() }
                .first()
            if (res is Success) _initialUserNameEvent.publishEvent(res.value.username)
        }
    }

    fun saveUserName(newUsername: String) = viewModelScope.safeLaunch {
        showProgress()
        try {
            accountsRepository.updateAccountUsername(newUsername)
            goBack()
        } catch (e: EmptyFieldException) {
            showEmptyFieldErrorMessage()
        }   finally {
            hideProgress()
        }
    }

    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _saveInProgress.value = true
    }

    private fun hideProgress() {
        _saveInProgress.value = false
    }

    private fun showEmptyFieldErrorMessage() = _showErrorEvent.publishEvent(R.string.field_is_empty)
}