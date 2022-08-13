package com.mcmouse.nav_tabs.models.accounts

import com.mcmouse.nav_tabs.models.AccountAlreadyExistException
import com.mcmouse.nav_tabs.models.AuthException
import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.Field
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryAccountsRepository : AccountsRepository {

    private val currentAccountFlow = MutableStateFlow<Account?>(null)

    private val accounts = mutableListOf(
        AccountRecord(
            account = Account(
                username = "Admin",
                email = "admin@google.com"
            ),
            password = "123456"
        )
    )

    init {
        currentAccountFlow.value = accounts[0].account
    }

    override suspend fun isSignedIn(): Boolean {
        delay(2_000)
        return currentAccountFlow.value != null
    }

    override suspend fun signIn(email: String, password: String) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        delay(1_000)
        val record = getRecordByEmail(email)

        if (record != null && record.password == password) {
            currentAccountFlow.value = record.account
        } else {
            throw AuthException()
        }
    }

    override suspend fun signUp(signUpData: SignUpData) {
        signUpData.validate()

        delay(1_000)
        val accountRecord = getRecordByEmail(signUpData.email)
        if (accountRecord != null) throw AccountAlreadyExistException()

        val newAccount = Account(
            username = signUpData.username,
            email = signUpData.email,
            createdAt = System.currentTimeMillis()
        )
        accounts.add(AccountRecord(newAccount, signUpData.password))
    }

    override fun logout() {
        currentAccountFlow.value = null
    }

    override fun getAccount(): Flow<Account?> = currentAccountFlow

    override suspend fun updateAccountUsername(newUsername: String) {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)

        delay(1_000)
        val currentAccount = currentAccountFlow.value ?: throw AuthException()

        val updateAccount = currentAccount.copy(username = newUsername)
        currentAccountFlow.value = updateAccount
        val currentRecord = getRecordByEmail(currentAccount.email) ?: throw AuthException()
        currentRecord.account = updateAccount
    }

    private fun getRecordByEmail(email: String) = accounts.firstOrNull {
        it.account.email == email
    }

    private class AccountRecord(
        var account: Account,
        val password: String
    )
}