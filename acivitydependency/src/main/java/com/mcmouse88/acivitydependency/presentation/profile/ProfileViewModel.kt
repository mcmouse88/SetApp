package com.mcmouse88.acivitydependency.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.acivitydependency.domain.ErrorHandler
import com.mcmouse88.acivitydependency.domain.accounts.Account
import com.mcmouse88.acivitydependency.domain.accounts.AccountsRepository
import com.mcmouse88.acivitydependency.domain.launchIn
import com.mcmouse88.acivitydependency.presentation.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mcmouse88.acivitydependency.domain.Result
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountRepository: AccountsRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _accountLiveData = MutableLiveData<Account>()
    val accountLiveData = _accountLiveData.share()

    init {
        viewModelScope.launch {
            accountRepository.getAccount()
                .filterIsInstance<Result.Success<Account>>()
                .collectLatest {
                    _accountLiveData.value = it.value
                }
        }
    }

    fun signOut() {
        errorHandler.launchIn(viewModelScope) {
            accountRepository.signOut()
        }
    }
}