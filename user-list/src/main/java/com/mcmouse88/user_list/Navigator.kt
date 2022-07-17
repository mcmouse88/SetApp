package com.mcmouse88.user_list

import com.mcmouse88.user_list.model.User

interface Navigator {

    fun showDetail(user: User)

    fun goBack()

    fun toast(message: String)
}