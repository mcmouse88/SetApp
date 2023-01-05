package com.mcmouse88.remote_mediator.ui

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mcmouse88.remote_mediator.R
import com.mcmouse88.remote_mediator.domain.Launch
import com.mcmouse88.remote_mediator.domain.LaunchesRepository
import com.mcmouse88.remote_mediator.ui.base.MutableLiveEvent
import com.mcmouse88.remote_mediator.ui.base.publishEvent
import com.mcmouse88.remote_mediator.ui.base.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val launchesRepository: LaunchesRepository
) : ViewModel() {

    private val selection = Selections()
    private val yearLiveData = savedStateHandle.getLiveData(KEY_YEAR, 2020)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val launchesFlow = yearLiveData.asFlow()
        .distinctUntilChanged()
        .flatMapLatest {
            launchesRepository.getLaunches(it)
        }.cachedIn(viewModelScope)

    private val _toastEvent = MutableLiveEvent<Int>()
    val toastEvent = _toastEvent.share()

    val launchesListFlow = combine(
        launchesFlow,
        selection.flow(),
        ::merge
    )

    var year: Int?
        get() = yearLiveData.value
        set(value) {
            yearLiveData.value = value
        }

    private fun merge(
        pagingData: PagingData<Launch>,
        selections: SelectionState
    ): PagingData<LaunchUiEntity> {
        return pagingData.map { launch ->
            LaunchUiEntity(
                launch = launch,
                isChecked = selections.isChecked(launch.id)
            )
        }
    }

    fun toggleCheckState(launch: LaunchUiEntity) {
        selection.toggle(launch.id)
    }

    fun toggleSuccessFlag(launch: LaunchUiEntity) = viewModelScope.launch {
        try {
            launchesRepository.toggleSuccessFlag(launch)
        } catch (e: Exception) {
            showToast(R.string.oops)
        }
    }

    private fun showToast(@StringRes messageRes: Int) {
        _toastEvent.publishEvent(messageRes)
    }

    private companion object {
        const val KEY_YEAR = "KEY_YEAR"
    }
}