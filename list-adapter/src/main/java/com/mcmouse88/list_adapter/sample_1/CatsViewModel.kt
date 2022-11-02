package com.mcmouse88.list_adapter.sample_1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.list_adapter.model.Cat
import com.mcmouse88.list_adapter.model.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<List<Cat>>()
    val catsLiveData: LiveData<List<Cat>> get() = _catsLiveData

    init {
        viewModelScope.launch {
            catsRepository.getCats().collectLatest { catList ->
                _catsLiveData.value = catList
            }
        }
    }

    fun deleteCat(cat: Cat) {
        catsRepository.delete(cat)
    }

    fun toggleFavorite(cat: Cat) {
        catsRepository.toggleIsFavorite(cat)
    }
}