package com.mcmouse88.user_list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcmouse88.user_list.model.User
import com.mcmouse88.user_list.model.UserListener
import com.mcmouse88.user_list.model.UserService

class UserListViewModel(
    private val userService: UserService
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    private val listener: UserListener = {
        _users.value = it
    }

    init {
        loadUsers()
    }

    private fun loadUsers() {
        userService.addListener(listener)
    }

    fun moveUser(user: User, moveBy: Int) {
        userService.moveUser(user, moveBy)
    }

    fun deleteUser(user: User) {
        userService.deleteUser(user)
    }

    override fun onCleared() {
        userService.removeListener(listener)
        super.onCleared()
    }
}