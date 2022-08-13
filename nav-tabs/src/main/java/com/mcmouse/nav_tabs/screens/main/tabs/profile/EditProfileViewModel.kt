package com.mcmouse.nav_tabs.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.utils.MutableLiveEvent
import com.mcmouse.nav_tabs.utils.MutableUnitLiveEvent
import com.mcmouse.nav_tabs.utils.publishEvent
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    private val _initialUserNameEvent = MutableLiveEvent<String>()
    val initialUserNameEvent = _initialUserNameEvent.share()

    private val _saveInProgress = MutableLiveData(false)
    val saveInProgress = _saveInProgress.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    private val _showEmptyFieldErrorEvent = MutableUnitLiveEvent()
    val showEmptyFieldErrorEvent = _showEmptyFieldErrorEvent.share()

    init {
        viewModelScope.launch {
            val account = accountsRepository.getAccount()
                .filterNotNull()
                .first()
            _initialUserNameEvent.publishEvent(account.username)
        }
    }

    fun saveUsername(newUsername: String) {
        viewModelScope.launch {
            showProgress()
            try {
                accountsRepository.updateAccountUsername(newUsername)
                goBack()
            } catch (e: EmptyFieldException) {
                hideProgress()
                showEmptyFieldErrorMessage()
            }
        }
    }

    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _saveInProgress.value = true
    }

    private fun hideProgress() {
        _saveInProgress.value = false
    }

    private fun showEmptyFieldErrorMessage() = _showEmptyFieldErrorEvent.publishEvent()
}