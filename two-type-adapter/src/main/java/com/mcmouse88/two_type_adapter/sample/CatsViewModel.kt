package com.mcmouse88.two_type_adapter.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.two_type_adapter.models.Cat
import com.mcmouse88.two_type_adapter.models.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<List<BaseListItem>>()
    val catsLiveData: LiveData<List<BaseListItem>> get() = _catsLiveData

    init {
        viewModelScope.launch {
            catsRepository.getCats().collectLatest { catsList ->
                _catsLiveData.value = mapCats(catsList)
            }
        }
    }

    fun deleteCat(cat: BaseListItem.CatItem) {
        catsRepository.delete(cat.originCat)
    }

    fun toggleFavorite(cat: BaseListItem.CatItem) {
        catsRepository.toggleIsFavorite(cat.originCat)
    }

    private fun mapCats(cats: List<Cat>): List<BaseListItem> {
        val size = 10
        return cats
            .chunked(size)
            .mapIndexed { index, list ->
                val fromIndex = index * size + 1
                val toIndex = fromIndex + list.size - 1
                val header: BaseListItem = BaseListItem.HeaderItem(index, fromIndex, toIndex)
                listOf(header) + list.map(BaseListItem::CatItem)
            }.flatten()
    }
}