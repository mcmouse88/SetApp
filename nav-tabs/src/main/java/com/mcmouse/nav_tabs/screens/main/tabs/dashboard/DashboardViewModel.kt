package com.mcmouse.nav_tabs.screens.main.tabs.dashboard

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.models.boxes.BoxesRepository
import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val boxesRepository: BoxesRepository
) : ViewModel() {

    private val _boxes = MediatorLiveData<List<Box>>()
    val boxes = _boxes.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxes(onlyActive = true).collect {
                _boxes.value = it
            }
        }
    }
}