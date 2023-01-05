package com.mcmouse88.user_list

import android.app.Application
import com.mcmouse88.user_list.model.UserService

class App : Application() {
    val userService: UserService = UserService()
}