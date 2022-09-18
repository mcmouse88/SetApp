package com.mcmouse88.okhttp.app.screens.main.tabs.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mcmouse88.okhttp.app.model.Success
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.model.boxes.BoxesRepository
import com.mcmouse88.okhttp.app.model.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.app.screens.base.BaseViewModel
import com.mcmouse88.okhttp.app.utiils.MutableLiveEvent
import com.mcmouse88.okhttp.app.utiils.logger.Logger
import com.mcmouse88.okhttp.app.utiils.publishEvent
import com.mcmouse88.okhttp.app.utiils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Так как у нас в конструкторе [ViewModel] есть некий аргумент типа [Long], про который hilt
 * ничего не знает, а именно про то как его нужно передать в конструктор. Вместо него мы можем
 * передать в конструктор [SavedStateHandle]. Второй вариант это использование даггеровской
 * аннотации [AssistedInject] вместо обычного [Inject]. Поле которое нужно заинжектить
 * помечается аннотацией [Assisted]
 */
// @HiltViewModel
class BoxViewModel @AssistedInject constructor(
    @Assisted("boxId") private val boxId: Long,
    // savedStateHandle: SavedStateHandle,
    private val boxesRepository: BoxesRepository,
    accountsRepository: AccountsRepository,
    logger: Logger
) : BaseViewModel(accountsRepository, logger) {

    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()

    /**
     * А уже таким способом можно получить аргумент id, который ранее передавали в конструктор
     * [ViewModel]
     */
    /*private val navArgs = BoxFragmentArgs
        .fromSavedStateHandle(savedStateHandle)*/

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE)
                .map { res -> res.mapResult { boxes -> boxes.firstOrNull { it.box.id == boxId } } }
                .collect { res ->
                    _shouldExitEvent.publishEvent(res is Success && res.value == null)
                }
        }
    }

    /**
     * Для [AssistedInject] также нужно написать фабрику, при помощи которой зависимость будет
     * доставляться в конструктор [ViewModel].
     */
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("boxId") boxId: Long): BoxViewModel
    }
}