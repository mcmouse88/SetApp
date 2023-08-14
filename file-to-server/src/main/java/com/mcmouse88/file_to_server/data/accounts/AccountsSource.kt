package com.mcmouse88.file_to_server.data.accounts

import com.mcmouse88.file_to_server.domain.accounts.Account

interface AccountsSource {

    suspend fun oauthSignIn(): Account

    suspend fun getAccount(): Account

    suspend fun signOut()
}