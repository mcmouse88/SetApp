package com.mcmouse88.acivitydependency.domain.accounts

interface AccountSource {

    suspend fun oauthSignIn(): Account

    suspend fun getAccount(): Account

    suspend fun signOut()
}