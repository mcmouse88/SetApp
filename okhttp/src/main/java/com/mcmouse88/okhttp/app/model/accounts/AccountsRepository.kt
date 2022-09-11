package com.mcmouse88.okhttp.app.model.accounts

import com.mcmouse88.okhttp.app.model.*
import com.mcmouse88.okhttp.app.model.accounts.entities.Account
import com.mcmouse88.okhttp.app.model.accounts.entities.SignUpData
import com.mcmouse88.okhttp.app.model.settings.AppSettings
import com.mcmouse88.okhttp.app.model.wrapBackendException
import com.mcmouse88.okhttp.app.utiils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow

class AccountsRepository(
    private val accountsSource: AccountsSource,
    private val appSettings: AppSettings
) {

    private val accountLazyFlowSubject = LazyFlowSubject<Unit, Account> {
        doGetAccount()
    }

    fun isSignedIn(): Boolean = appSettings.getCurrentToken() != null

    suspend fun signIn(email: String, password: String) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        val token = try {
            accountsSource.signIn(email, password)
        } catch (e: Exception) {
            if (e is BackendException && e.code == 401) {
                throw InvalidCredentialsException(e)
            } else throw e
        }

        appSettings.setCurrentToken(token)
        accountLazyFlowSubject.updateAllValues(accountsSource.getAccount())
    }

    suspend fun signUp(signUpData: SignUpData) {
        signUpData.validate()
        try {
            accountsSource.signUp(signUpData)
        } catch (e: BackendException) {
            if (e.code == 409) throw AccountAlreadyExistException(e)
            else throw e
        }
    }

    fun reloadAccount() {
        accountLazyFlowSubject.reloadAll()
    }

    fun getAccount(): Flow<ResultResponse<Account>> {
        return accountLazyFlowSubject.listen(Unit)
    }

    suspend fun updateAccountUsername(newUsername: String) = wrapBackendException {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)
        accountsSource.setUsername(newUsername)
        accountLazyFlowSubject.updateAllValues(accountsSource.getAccount())
    }

    fun logout() {
        appSettings.setCurrentToken(null)
        accountLazyFlowSubject.updateAllValues(null)
    }

    private suspend fun doGetAccount(): Account = wrapBackendException {
        try {
            accountsSource.getAccount()
        } catch (e: BackendException) {
            if (e.code == 404) throw AuthException(e)
            else throw e
        }
    }
}