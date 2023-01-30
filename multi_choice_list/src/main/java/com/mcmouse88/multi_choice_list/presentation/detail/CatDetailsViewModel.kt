package com.mcmouse88.multi_choice_list.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.multi_choice_list.domain.Cat
import com.mcmouse88.multi_choice_list.domain.CatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CatDetailsViewModel @AssistedInject constructor(
    private val catsRepository: CatsRepository,
    @Assisted catId: Long
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<Cat>()
    val catsLiveData: LiveData<Cat> get() = _catsLiveData

    init {
        viewModelScope.launch {
            catsRepository.getCatById(catId).filterNotNull().collect {
                _catsLiveData.value = it
            }
        }
    }

    fun toggleFavorite() {
        val cat = _catsLiveData.value ?: return
        catsRepository.toggleIsFavorite(cat.id)
    }

    @AssistedFactory
    interface Factory {
        fun create(catId: Long): CatDetailsViewModel
    }
}