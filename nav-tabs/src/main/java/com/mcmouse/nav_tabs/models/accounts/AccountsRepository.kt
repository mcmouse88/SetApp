package com.mcmouse.nav_tabs.models.accounts

import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {

    suspend fun isSignedIn(): Boolean

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(signUpData: SignUpData)

    fun logout()

    fun getAccount(): Flow<Account?>

    suspend fun updateAccountUsername(newUsername: String)
}