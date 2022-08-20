package com.mcmouse.nav_tabs.models.accounts.room

import android.database.sqlite.SQLiteConstraintException
import com.mcmouse.nav_tabs.models.AccountAlreadyExistException
import com.mcmouse.nav_tabs.models.AuthException
import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.Field
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.models.accounts.entities.AccountFullData
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountDbEntity
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountUpdateUserNameTuple
import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings
import com.mcmouse.nav_tabs.models.room.wrapSQLiteException
import com.mcmouse.nav_tabs.models.settings.AppSettings
import com.mcmouse.nav_tabs.utils.AsyncLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class RoomAccountsRepository(
    private val accountsDao: AccountsDao,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }

    override suspend fun isSignedIn(): Boolean {
        delay(2_000)
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
    }

    override suspend fun signIn(email: String, password: CharArray) = wrapSQLiteException(ioDispatcher) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isEmpty()) throw EmptyFieldException(Field.Password)

        delay(1_000)

        val accountId = findAccountIdByEmailAndPassword(email, password)
        appSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)
    }

    override suspend fun signUp(signUpData: SignUpData) = wrapSQLiteException(ioDispatcher) {
        signUpData.validate()
        delay(1_000)
        createAccount(signUpData)
    }

    override suspend fun logout() {
        appSettings.setCurrentAccountId(AppSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(AppSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .flatMapLatest {
                if (it.value == AppSettings.NO_ACCOUNT_ID) {
                    flowOf(null)
                } else {
                    getAccountById(it.value)
                }
            }.flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUsername: String) = wrapSQLiteException(ioDispatcher) {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)

        delay(1_000)
        val accountId = appSettings.getCurrentAccountId()
        if (accountId == AppSettings.NO_ACCOUNT_ID) throw AuthException()

        updateUserNameForAccountId(accountId, newUsername)
        currentAccountIdFlow.get().value = AccountId(accountId)
        return@wrapSQLiteException
    }

    override suspend fun getAllData(): Flow<List<AccountFullData>> {
        val account = getAccount().first()
        if (account == null || !account.isAdmin()) throw AuthException()
        return accountsDao.getAllData()
            .map { accountsAndSettings ->
                accountsAndSettings.map { accountsAndSettingsTuple ->
                    AccountFullData(
                        account = accountsAndSettingsTuple.accountDbEntity.toAccount(),
                        boxAndSettings = accountsAndSettingsTuple.settings.map {
                            BoxAndSettings(
                                box = it.boxDbEntity.toBox(),
                                isActive = it.accountAndAllSettingsTuple.settings.isActive
                            )
                        }
                    )
                }
            }
    }

    private suspend fun findAccountIdByEmailAndPassword(email: String, password: CharArray): Long {
        val tuple = accountsDao.findByEmail(email) ?: throw AuthException()
        if (!tuple.password.toCharArray().contentEquals(password)) throw AuthException()
        return tuple.userId
    }

    private suspend fun createAccount(signUpData: SignUpData) {
        try {
            val entity = AccountDbEntity.fromSignUpData(signUpData)
            accountsDao.createAccount(entity)
        } catch (e: SQLiteConstraintException) {
            val appException = AccountAlreadyExistException()
            appException.initCause(e)
            throw appException
        }
    }

    private fun getAccountById(accountId: Long): Flow<Account?> {
        return accountsDao.getById(accountId).map { it?.toAccount() }
    }

    private suspend fun updateUserNameForAccountId(accountId: Long, newUsername: String) {
        accountsDao.updateUsername(
            AccountUpdateUserNameTuple(accountId, newUsername)
        )
    }

    private class AccountId(val value: Long)
}