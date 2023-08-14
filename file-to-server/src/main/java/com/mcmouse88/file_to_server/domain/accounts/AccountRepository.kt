package com.mcmouse88.file_to_server.domain.accounts

import com.mcmouse88.file_to_server.data.accounts.AccountsSource
import com.mcmouse88.file_to_server.domain.AuthException
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.mcmouse88.file_to_server.domain.Result
import com.mcmouse88.file_to_server.domain.ignoreErrors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription

@Singleton
class AccountRepository @Inject constructor(
    private val accountsSource: AccountsSource
) {

    private val accountsFlow = MutableStateFlow<Result<Account>>(Result.Pending())

    fun getAccount(): Flow<Result<Account>> {
        return accountsFlow
            .onSubscription {
                emit(Result.Pending())
                ignoreErrors { reloadAccount(silently = true) }
            }
    }

    suspend fun reloadAccount(silently: Boolean = false) {
        try {
            if (silently.not()) accountsFlow.value = Result.Pending()
            accountsFlow.value = Result.Success(accountsSource.getAccount())
        } catch (e: Exception) {
            accountsFlow.value = Result.Error(e)
            throw e
        }
    }

    suspend fun oauthSignIn() {
        val account = accountsSource.oauthSignIn()
        accountsFlow.value = Result.Success(account)
    }

    suspend fun signOut() {
        accountsSource.signOut()
        accountsFlow.value = Result.Error(AuthException())
    }
}