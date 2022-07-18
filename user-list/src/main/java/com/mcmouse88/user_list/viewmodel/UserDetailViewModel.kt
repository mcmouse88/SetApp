package com.mcmouse88.user_list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mcmouse88.user_list.R
import com.mcmouse88.user_list.model.UserDetail
import com.mcmouse88.user_list.model.UserService
import com.mcmouse88.user_list.screens.tasks.EmptyResult
import com.mcmouse88.user_list.screens.tasks.PendingResult
import com.mcmouse88.user_list.screens.tasks.Result
import com.mcmouse88.user_list.screens.tasks.SuccessResult

class UserDetailViewModel(
    private val userService: UserService,
    private val userId: Long
) : BaseViewModel() {

    private val _state = MutableLiveData<StateUserDetailFragment>()
    val state: LiveData<StateUserDetailFragment> = this._state

    private val _actionShowToast = MutableLiveData<EventInViewModel<Int>>()
    val actionShowToast: LiveData<EventInViewModel<Int>>
        get() = _actionShowToast

    private val _actionGoBack = MutableLiveData<EventInViewModel<Unit>>()
    val actionGoBack: LiveData<EventInViewModel<Unit>> = _actionGoBack

    private val currentState: StateUserDetailFragment
        get() = state.value ?: throw RuntimeException("State in UserDetailViewModel incorrect")

    init {
        _state.value = StateUserDetailFragment(
            userDetailResult = EmptyResult(),
            deletingInProgress = false
        )
        loadUser()
    }

    fun deleteUser() {
        val userDetailResult = currentState.userDetailResult
        if (userDetailResult !is SuccessResult) return
        _state.value = currentState.copy(deletingInProgress = true)
        userService.deleteUser(userDetailResult.data.user)
            .onSuccess {
                _actionShowToast.value = EventInViewModel(R.string.user_has_been_delete)
                _actionGoBack.value = EventInViewModel(Unit)
            }
            .onError {
                _state.value = currentState.copy(deletingInProgress = false)
                _actionShowToast.value = EventInViewModel(R.string.cant_delete_user)
            }
            .autoCancel()
    }

    private fun loadUser() {
        // if (currentState.userDetailResult is SuccessResult) return
        if (currentState.userDetailResult !is EmptyResult) return

        _state.value = currentState.copy(userDetailResult = PendingResult())
        userService.getUserById(userId)
            .onSuccess {
                _state.value = currentState.copy(userDetailResult = SuccessResult(it))
            }
            .onError {
                _actionShowToast.value = EventInViewModel(R.string.cant_load_user_details)
                _actionGoBack.value = EventInViewModel(Unit)
            }
            .autoCancel()
    }

    data class StateUserDetailFragment(
        val userDetailResult: Result<UserDetail>,
        private val deletingInProgress: Boolean
    ) {
        val showContent: Boolean get() = userDetailResult is SuccessResult
        val showProgress: Boolean get() = userDetailResult is PendingResult || deletingInProgress
        val enableDeleteButton: Boolean get() = !deletingInProgress
    }
}