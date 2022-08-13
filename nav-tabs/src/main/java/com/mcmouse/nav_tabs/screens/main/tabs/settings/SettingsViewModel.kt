package com.mcmouse.nav_tabs.screens.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.boxes.BoxesRepository
import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val boxesRepository: BoxesRepository
) : ViewModel(), SettingsAdapter.Listener {

    private val _boxSetting = MutableLiveData<List<BoxSetting>>()
    val boxSetting = _boxSetting.share()

    init {
        viewModelScope.launch {
            val allBoxesFlow = boxesRepository.getBoxes(onlyActive = false)
            val activateBoxesFlow = boxesRepository.getBoxes(onlyActive = true)
            val boxSettingFlow = combine(allBoxesFlow, activateBoxesFlow) { allBoxes, activeBoxes ->
                allBoxes.map { BoxSetting(it, activeBoxes.contains(it)) }
            }
            boxSettingFlow.collect() {
                _boxSetting.value = it
            }
        }
    }

    override fun enableBox(box: Box) {
        viewModelScope.launch { boxesRepository.activateBox(box) }
    }

    override fun disableBox(box: Box) {
        viewModelScope.launch { boxesRepository.deactivateBox(box) }
    }
}