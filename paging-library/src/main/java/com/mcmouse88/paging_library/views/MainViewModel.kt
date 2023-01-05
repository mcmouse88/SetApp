package com.mcmouse88.paging_library.views

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mcmouse88.paging_library.MutableLiveEvent
import com.mcmouse88.paging_library.R
import com.mcmouse88.paging_library.adapters.UsersAdapter
import com.mcmouse88.paging_library.model.users.User
import com.mcmouse88.paging_library.model.users.repositories.UsersRepository
import com.mcmouse88.paging_library.publishEvent
import com.mcmouse88.paging_library.share
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val usersRepository: UsersRepository
) : ViewModel(), UsersAdapter.Listener {

    val isErrorsEnabled: Flow<Boolean> = usersRepository.isErrorEnabled()

    val usersFlow: Flow<PagingData<UserListItem>>

    private val searchBy = MutableLiveData("")

    private val localChanges = LocalChanges()
    private val localChangesFlow = MutableStateFlow(OnChange(localChanges))

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    init {
        val originUsersFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                usersRepository.getPagedUsers(it)
            }.cachedIn(viewModelScope)

        usersFlow = combine(
            originUsersFlow,
            localChangesFlow.debounce(50),
            this::merge
        )
    }

    override fun onUserDelete(userListItem: UserListItem) {
        if (isInProgress(userListItem)) return
        viewModelScope.launch {
            try {
                setProgress(userListItem, true)
                delete(userListItem)
            } catch (e: Exception) {
                showError(R.string.error_delete)
            } finally {
                setProgress(userListItem, false)
            }
        }
    }

    override fun onToggleFavoriteFlag(userListItem: UserListItem) {
        if (isInProgress(userListItem)) return
        viewModelScope.launch {
            try {
                setProgress(userListItem, true)
                setFavoriteFlag(userListItem)
            } catch (e: Exception) {
                showError(R.string.error_change_favorite)
            } finally {
                setProgress(userListItem, false)
            }
        }
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
        scrollListToTop()
    }

    fun refresh() {
        searchBy.postValue(searchBy.value)
    }

    fun setEnableErrors(value: Boolean) {
        usersRepository.setErrorEnabled(value)
    }

    private fun setProgress(userListItem: UserListItem, inProgress: Boolean) {
        if (inProgress) {
            localChanges.idsInProgress.add(userListItem.id)
        } else {
            localChanges.idsInProgress.remove(userListItem.id)
        }
        localChangesFlow.value = OnChange(localChanges)
    }

    private fun isInProgress(userListItem: UserListItem) =
        localChanges.idsInProgress.contains(userListItem.id)

    private suspend fun setFavoriteFlag(userListItem: UserListItem) {
        val newFlagValue = !userListItem.isFavorite
        usersRepository.setIsFavorite(userListItem.user, newFlagValue)
        localChanges.favoriteFlags[userListItem.id] = newFlagValue
        localChangesFlow.value = OnChange(localChanges)
    }

    private suspend fun delete(userListItem: UserListItem) {
        usersRepository.delete(userListItem.user)
        invalidateList()
    }

    private fun showError(@StringRes errorMessage: Int) {
        _errorEvents.publishEvent(errorMessage)
    }

    private fun scrollListToTop() {
        _scrollEvents.publishEvent(Unit)
    }

    private fun invalidateList() {
        _invalidateEvents.publishEvent(Unit)
    }

    private fun merge(users: PagingData<User>, localChanges: OnChange<LocalChanges>): PagingData<UserListItem> {
        return users.map { user ->
            val isInProgress = localChanges.value.idsInProgress.contains(user.id)
            val localFavoriteFlag = localChanges.value.favoriteFlags[user.id]

            val userWithLocalChange = if (localFavoriteFlag == null) user
            else user.copy(isFavorite = localFavoriteFlag)
            UserListItem(userWithLocalChange, isInProgress)
        }
    }

    class OnChange<T>(val value: T)

    class LocalChanges {
        val idsInProgress = mutableSetOf<Long>()
        val favoriteFlags = mutableMapOf<Long, Boolean>()
    }
}