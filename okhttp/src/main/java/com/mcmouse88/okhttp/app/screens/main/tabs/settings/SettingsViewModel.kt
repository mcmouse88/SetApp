package com.mcmouse88.okhttp.app.screens.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.ResultResponse
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.model.boxes.BoxesRepository
import com.mcmouse88.okhttp.app.model.boxes.entities.Box
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.app.screens.base.BaseViewModel
import com.mcmouse88.okhttp.app.utiils.logger.LogcatLogger
import com.mcmouse88.okhttp.app.utiils.logger.Logger
import com.mcmouse88.okhttp.app.utiils.share
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val boxesRepository: BoxesRepository = Singletons.boxesRepository,
    accountsRepository: AccountsRepository = Singletons.accountsRepository,
    logger: Logger = LogcatLogger
) : BaseViewModel(accountsRepository, logger), SettingsAdapter.Listener {

    private val _boxSetting = MutableLiveData<ResultResponse<List<BoxAndSettings>>>()
    val boxSetting = _boxSetting.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ALL).collect {
                _boxSetting.value = it
            }
        }
    }

    override fun enableBox(box: Box) = viewModelScope.safeLaunch {
        boxesRepository.activateBox(box)
    }

    override fun disableBox(box: Box) = viewModelScope.safeLaunch {
        boxesRepository.deactivateBox(box)
    }

    fun tryAgain() = viewModelScope.safeLaunch {
        boxesRepository.reload(BoxesFilter.ALL)
    }
}