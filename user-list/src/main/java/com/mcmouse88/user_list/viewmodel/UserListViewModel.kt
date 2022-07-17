package com.mcmouse88.user_list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcmouse88.user_list.R
import com.mcmouse88.user_list.model.User
import com.mcmouse88.user_list.model.UserActionListener
import com.mcmouse88.user_list.model.UserListener
import com.mcmouse88.user_list.model.UserService
import com.mcmouse88.user_list.screens.tasks.*

data class UserListItem(
    val user: User,
    val isProgress: Boolean
)

class UserListViewModel(
    private val userService: UserService
) : BaseViewModel(), UserActionListener {

    private val _users = MutableLiveData<Result<List<UserListItem>>>()
    val users: LiveData<Result<List<UserListItem>>>
        get() = _users

    private val _actionShowDetails = MutableLiveData<EventInViewModel<User>>()
    val actionShowDetails: LiveData<EventInViewModel<User>>
        get() = _actionShowDetails

    private val _actionShowToast = MutableLiveData<EventInViewModel<Int>>()
    val actionShowToast: LiveData<EventInViewModel<Int>>
        get() = _actionShowToast

    private val userIdsInProgress = mutableSetOf<Long>()
    private var userResult: Result<List<User>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: UserListener = {
        userResult = if (it.isEmpty()) EmptyResult()
        else SuccessResult(it)
    }

    init {
        userService.addListener(listener)
        loadUsers()
    }

    private fun loadUsers() {
        userResult = PendingResult()
        userService.loadUsers()
            .onError {
                userResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onUserMove(user: User, moveBy: Int) {
        if (isInProgress(user)) return
        addProgress(user)
        userService.moveUser(user, moveBy)
            .onSuccess { removeProgressFrom(user) }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = EventInViewModel(R.string.cant_move_user)
            }
            .autoCancel()
    }

    override fun onUserDelete(user: User) {
        if (isInProgress(user)) return
        addProgress(user)
        userService.deleteUser(user)
            .onSuccess { removeProgressFrom(user) }
            .onError {
                removeProgressFrom(user)
                _actionShowToast.value = EventInViewModel(R.string.cant_delete_user)
            }
            .autoCancel()
    }

    override fun onUserDetail(user: User) {
        _actionShowDetails.value = EventInViewModel(user)
    }

    private fun notifyUpdates() {
        _users.postValue(userResult.map { users ->
            users.map { user -> UserListItem(user, isInProgress(user)) }
        })
    }

    private fun addProgress(user: User) {
        userIdsInProgress.add(user.id)
        notifyUpdates()
    }

    private fun removeProgressFrom(user: User) {
        userIdsInProgress.remove(user.id)
        notifyUpdates()
    }

    private fun isInProgress(user: User): Boolean {
        return userIdsInProgress.contains(user.id)
    }

    override fun onCleared() {
        userService.removeListener(listener)
        super.onCleared()
    }
}