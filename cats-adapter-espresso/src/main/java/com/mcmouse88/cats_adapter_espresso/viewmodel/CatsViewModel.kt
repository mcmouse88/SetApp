package com.mcmouse88.cats_adapter_espresso.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mcmouse88.cats_adapter_espresso.model.Cat
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import com.mcmouse88.cats_adapter_espresso.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsRepository: CatsRepository
) : BaseViewModel() {

    val catsLiveData: LiveData<List<CatListItem>> = liveData()

    init {
        viewModelScope.launch {
            catsRepository.getCats().collectLatest { catList ->
                catsLiveData.update(mapCats(catList))
            }
        }
    }

    fun deleteCat(cat: CatListItem.CatItem) {
        catsRepository.delete(cat.originCat)
    }

    fun toggleCat(cat: CatListItem.CatItem) {
        catsRepository.toggleIsFavorite(cat.originCat)
    }

    private fun mapCats(cats: List<Cat>): List<CatListItem> {
        val size = 10
        return cats
            .chunked(size)
            .mapIndexed { index, list ->
                val fromIndex = index * size + 1
                val toIndex = fromIndex + list.size - 1
                val header: CatListItem = CatListItem.Header(index, fromIndex, toIndex)
                listOf(header) + list.map { CatListItem.CatItem(it) }
            }.flatten()
    }
}