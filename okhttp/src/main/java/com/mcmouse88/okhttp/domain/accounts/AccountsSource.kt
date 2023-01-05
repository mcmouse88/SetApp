package com.mcmouse88.okhttp.domain.accounts

import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.domain.accounts.entities.SignUpData

interface AccountsSource {

    suspend fun signIn(email: String, password: String): String

    suspend fun signUp(signUpData: SignUpData)

    suspend fun getAccount(): Account

    suspend fun setUsername(username: String)
}