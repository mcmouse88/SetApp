package com.mcmouse88.user_list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcmouse88.user_list.UserNotFoundException
import com.mcmouse88.user_list.model.UserDetail
import com.mcmouse88.user_list.model.UserService

class UserDetailViewModel(
    private val userService: UserService
) : ViewModel() {

    private val _userDetails = MutableLiveData<UserDetail>()
    val userDetails: LiveData<UserDetail> = _userDetails

    fun loadUser(userId: Long) {
        if (_userDetails.value != null) return
        try {
            _userDetails.value = userService.getUserById(userId)
        } catch (e: UserNotFoundException) {
            e.printStackTrace()
        }

    }

    fun deleteUser() {
        val userDetails = userDetails.value ?: return
        userService.deleteUser(userDetails.user)
    }
}