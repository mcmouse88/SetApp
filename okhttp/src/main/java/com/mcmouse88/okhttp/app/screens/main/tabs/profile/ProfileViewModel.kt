package com.mcmouse88.okhttp.app.screens.main.tabs.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.ResultResponse
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.model.accounts.entities.Account
import com.mcmouse88.okhttp.app.screens.base.BaseViewModel
import com.mcmouse88.okhttp.app.utiils.logger.LogcatLogger
import com.mcmouse88.okhttp.app.utiils.logger.Logger
import com.mcmouse88.okhttp.app.utiils.share
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountsRepository: AccountsRepository = Singletons.accountsRepository,
    logger: Logger = LogcatLogger
) : BaseViewModel(accountsRepository, logger) {

    private val _account = MutableLiveData<ResultResponse<Account>>()
    val account = _account.share()

    init {
        viewModelScope.launch {
            accountsRepository.getAccount().collect {
                _account.value = it
            }
        }
    }

    fun reload() {
        accountsRepository.reloadAccount()
    }
}