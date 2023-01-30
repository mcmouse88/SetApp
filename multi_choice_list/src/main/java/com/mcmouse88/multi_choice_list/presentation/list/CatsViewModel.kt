package com.mcmouse88.multi_choice_list.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse88.multi_choice_list.R
import com.mcmouse88.multi_choice_list.di.CatsMultiChoice
import com.mcmouse88.multi_choice_list.domain.Cat
import com.mcmouse88.multi_choice_list.domain.CatsRepository
import com.mcmouse88.multi_choice_list.multi_choice.MultiChoiceHandler
import com.mcmouse88.multi_choice_list.multi_choice.MultiChoiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsRepository: CatsRepository,
    @CatsMultiChoice private val multiChoiceHandler: MultiChoiceHandler<Cat>
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    init {
        viewModelScope.launch {
            multiChoiceHandler.setItemFlow(viewModelScope, catsRepository.getCats())
            val combinedFlow = combine(
                catsRepository.getCats(),
                multiChoiceHandler.listen(),
                ::merge
            )
            combinedFlow.collectLatest {
                _stateLiveData.value = it
            }
        }
    }

    fun deleteCat(cat: CatListItem) {
        catsRepository.delete(catId = cat.id)
    }

    fun toggleFavorite(cat: CatListItem) {
        catsRepository.toggleIsFavorite(catId = cat.id)
    }

    fun toggleSelection(cat: CatListItem) {
        multiChoiceHandler.toggle(cat.originCat)
    }

    fun selectOrClear() {
        _stateLiveData.value?.selectAllOperation?.operation?.invoke()
    }

    fun deleteSelectedCats() {
        viewModelScope.launch {
            val currentMultiChoiceState = multiChoiceHandler.listen().first()
            catsRepository.deleteSelectedCats(currentMultiChoiceState)
        }
    }

    private fun merge(cats: List<Cat>, multiChoiceState: MultiChoiceState<Cat>): State {
        return State(
            cats = cats.map { cat ->
                CatListItem(cat, multiChoiceState.isChecked(cat))
            },
            totalCount = cats.size,
            totalCheckedCount = multiChoiceState.totalCheckedCount,
            selectAllOperation = if (multiChoiceState.totalCheckedCount < cats.size) {
                SelectAllOperation(R.string.select_all, multiChoiceHandler::selectAll)
            } else {
                SelectAllOperation(R.string.clear_all, multiChoiceHandler::clearAll)
            }
        )
    }

    class SelectAllOperation(
        val titleRes: Int,
        val operation: () -> Unit
    )

    class State(
        val totalCount: Int,
        val totalCheckedCount: Int,
        val cats: List<CatListItem>,
        val selectAllOperation: SelectAllOperation
    )
}