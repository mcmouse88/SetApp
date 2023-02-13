package com.mcmouse88.acivitydependency.domain.accounts

import com.mcmouse88.acivitydependency.domain.AuthException
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.mcmouse88.acivitydependency.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

@Singleton
class AccountsRepository @Inject constructor(
    private val accountSource: AccountSource
) {

    private val accountsFlow = MutableStateFlow<Result<Account>>(Result.Pending())

    fun getAccount(): Flow<Result<Account>> {
        return accountsFlow
            .onStart {
                emit(Result.Pending())
                reloadAccount(silently = true)
            }
    }

    suspend fun reloadAccount(silently: Boolean = false) {
        try {
            if (silently.not()) accountsFlow.value = Result.Pending()
            accountsFlow.value = Result.Success(accountSource.getAccount())
        } catch (e: Exception) {
            accountsFlow.value = Result.Error(e)
        }
    }

    suspend fun oauthSignIn() {
        val account = accountSource.oauthSignIn()
        accountsFlow.value = Result.Success(account)
    }

    suspend fun signOut() {
        accountSource.signOut()
        accountsFlow.value = Result.Error(AuthException())
    }
}