package com.mcmouse88.adapterwithpayload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.adapterwithpayload.model.Cat
import com.mcmouse88.adapterwithpayload.model.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<List<CatListItem>>()
    val catsLiveData: LiveData<List<CatListItem>> get() = _catsLiveData

    init {
        viewModelScope.launch {
            catsRepository.getCats().collectLatest { catsList ->
                _catsLiveData.value = mapCats(catsList)
            }
        }
    }

    fun deleteCat(cat: CatListItem.CatItem) {
        catsRepository.delete(cat.originCat)
    }

    fun toggleCat(cat: CatListItem.CatItem) {
        catsRepository.toggleFavorite(cat.originCat)
    }

    private fun mapCats(cats: List<Cat>): List<CatListItem> {
        val size = 10
        return cats
            .chunked(size)
            .mapIndexed { index, list ->
                val fromIndex = index * size + 1
                val toIndex = fromIndex + list.size - 1
                val header: CatListItem = CatListItem.Header(index, fromIndex, toIndex)
                listOf(header) + list.map(CatListItem::CatItem)
            }.flatten()
    }
}