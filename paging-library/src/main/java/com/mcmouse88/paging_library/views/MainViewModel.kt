package com.mcmouse88.paging_library.views

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mcmouse88.paging_library.model.users.User
import com.mcmouse88.paging_library.model.users.repositories.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

    val isErrorsEnabled: Flow<Boolean> = usersRepository.isErrorEnabled()

    val usersFlow: Flow<PagingData<User>>

    private val searchBy = MutableLiveData("")

    init {
        usersFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                usersRepository.getPagedUsers(it)
            }.cachedIn(viewModelScope)
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    fun refresh() {
        searchBy.postValue(searchBy.value)
    }

    fun setEnableErrors(value: Boolean) {
        usersRepository.setErrorEnabled(value)
    }
}