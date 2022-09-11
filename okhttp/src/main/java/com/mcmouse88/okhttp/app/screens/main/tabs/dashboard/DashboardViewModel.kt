package com.mcmouse88.okhttp.app.screens.main.tabs.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.ResultResponse
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.model.boxes.BoxesRepository
import com.mcmouse88.okhttp.app.model.boxes.entities.Box
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.app.screens.base.BaseViewModel
import com.mcmouse88.okhttp.app.utiils.logger.LogcatLogger
import com.mcmouse88.okhttp.app.utiils.logger.Logger
import com.mcmouse88.okhttp.app.utiils.share
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val boxesRepository: BoxesRepository = Singletons.boxesRepository,
    accountsRepository: AccountsRepository = Singletons.accountsRepository,
    logger: Logger = LogcatLogger
) : BaseViewModel(accountsRepository, logger) {

    private val _boxes = MutableLiveData<ResultResponse<List<Box>>>()
    val boxes = _boxes.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).collect { result ->
                _boxes.value = result.mapResult { list -> list.map { it.box } }
            }
        }
    }

    fun reload() = viewModelScope.launch {
        boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)
    }
}