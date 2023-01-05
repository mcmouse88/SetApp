package com.mcmouse88.okhttp.presentation.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.domain.boxes.BoxesRepository
import com.mcmouse88.okhttp.domain.boxes.entities.Box
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.presentation.base.BaseViewModel
import com.mcmouse88.okhttp.utils.logger.Logger
import com.mcmouse88.okhttp.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val boxesRepository: BoxesRepository,
    accountsRepository: AccountsRepository,
    logger: Logger
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