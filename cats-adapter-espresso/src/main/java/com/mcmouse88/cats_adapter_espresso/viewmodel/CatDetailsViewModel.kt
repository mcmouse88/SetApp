package com.mcmouse88.cats_adapter_espresso.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.cats_adapter_espresso.model.Cat
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import com.mcmouse88.cats_adapter_espresso.viewmodel.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CatDetailsViewModel @AssistedInject constructor(
    private val catsRepository: CatsRepository,
    @Assisted catId: Long
) : BaseViewModel() {

    val catLiveData: LiveData<Cat> = liveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            catsRepository.getCatById(catId).filterNotNull().collect {
                catLiveData.update(it)
            }
        }
    }

    fun toggleFavorite() {
        val cat = catLiveData.value ?: return
        catsRepository.toggleIsFavorite(cat)
    }

    @AssistedFactory
    interface Factory {
        fun create(catId: Long): CatDetailsViewModel
    }
}