package com.mcmouse88.okhttp.app.screens.main.tabs.dashboard

import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.Success
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.model.boxes.BoxesRepository
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.app.screens.base.BaseViewModel
import com.mcmouse88.okhttp.app.utiils.MutableLiveEvent
import com.mcmouse88.okhttp.app.utiils.logger.LogcatLogger
import com.mcmouse88.okhttp.app.utiils.logger.Logger
import com.mcmouse88.okhttp.app.utiils.publishEvent
import com.mcmouse88.okhttp.app.utiils.share
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoxViewModel(
    private val boxId: Long,
    private val boxesRepository: BoxesRepository = Singletons.boxesRepository,
    private val accountsRepository: AccountsRepository = Singletons.accountsRepository,
    logger: Logger = LogcatLogger
) : BaseViewModel(accountsRepository, logger) {

    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)
                .map { res -> res.mapResult { boxes -> boxes.firstOrNull { it.box.id == boxId } } }
                .collect { res ->
                    _shouldExitEvent.publishEvent(res is Success && res.value == null)
                }
        }
    }
}