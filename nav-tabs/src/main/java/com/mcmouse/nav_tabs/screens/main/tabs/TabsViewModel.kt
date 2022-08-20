package com.mcmouse.nav_tabs.screens.main.tabs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TabsViewModel(
    accountsRepository: AccountsRepository
) : ViewModel() {

    private val _showAdminTab = MutableLiveData<Boolean>()
    val showAdminTab = _showAdminTab.share()

    init {
        viewModelScope.launch {
            accountsRepository.getAccount().collect {
                _showAdminTab.value = it?.isAdmin() == true
            }
        }
    }
}