package com.mcmouse.nav_tabs.models.accounts

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.mcmouse.nav_tabs.models.AccountAlreadyExistException
import com.mcmouse.nav_tabs.models.AuthException
import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.Field
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import com.mcmouse.nav_tabs.models.settings.AppSettings
import com.mcmouse.nav_tabs.models.sqlite.AppSQLiteContract
import com.mcmouse.nav_tabs.models.sqlite.AppSQLiteContract.AccountsTable
import com.mcmouse.nav_tabs.models.sqlite.wrapSQLiteException
import com.mcmouse.nav_tabs.utils.AsyncLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SQLiteAccountsRepository(
    private val database: SQLiteDatabase,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {

    private val currentAccountFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }

    override suspend fun isSignedIn(): Boolean {
        delay(2_000)
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
    }

    override suspend fun signIn(email: String, password: String) =
        wrapSQLiteException(ioDispatcher) {
            if (email.isBlank()) throw EmptyFieldException(Field.Email)
            if (password.isBlank()) throw EmptyFieldException(Field.Password)

            delay(1_000)

            val accountId = findAccountIdByEmailAndPassword(email, password)
            appSettings.setCurrentAccountId(accountId)
            currentAccountFlow.get().value = AccountId(accountId)

            return@wrapSQLiteException
        }

    override suspend fun signUp(signUpData: SignUpData) = wrapSQLiteException(ioDispatcher) {
        signUpData.validate()

        delay(1_000)
        createAccount(signUpData)
    }

    override suspend fun logout() {
        appSettings.setCurrentAccountId(AppSettings.NO_ACCOUNT_ID)
        currentAccountFlow.get().value = AccountId(AppSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountFlow.get()
            .map { accountId ->
                getAccountById(accountId.value)
            }.flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUsername: String) =
        wrapSQLiteException(ioDispatcher) {
            if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)

            delay(1_000)
            val accountId = appSettings.getCurrentAccountId()
            if (accountId == AppSettings.NO_ACCOUNT_ID) throw AuthException()

            updateUserNameForAccountId(accountId, newUsername)
            currentAccountFlow.get().value = AccountId(accountId)
            return@wrapSQLiteException
        }

    /**
     * Чтобы выполнить запрос SQL для чтения или записи данных есть два метода, [rawQuery()], в
     * котором в качестве параметра пишется запрос на чистом SQL, и метод [query()], который
     * более удобен для создания запросов со стороны андроид-разработчика.
     */
    private fun findAccountIdByEmailAndPassword(email: String, password: String): Long {
        val cursor = database.query(
            AccountsTable.TABLE_NAME,
            arrayOf(AccountsTable.COLUMN_ID, AccountsTable.COLUMN_PASSWORD),
            "${AccountsTable.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )
        return cursor.use {
            if (cursor.count == 0) throw AuthException()
            cursor.moveToFirst()
            val passwordFromDb = cursor.getString(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_PASSWORD))
            if (passwordFromDb != password) throw AuthException()
            cursor.getLong(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_ID))
        }
    }

    private fun createAccount(signUpData: SignUpData) {
        try {
            database.insertOrThrow(
                AccountsTable.TABLE_NAME,
                null,
                contentValuesOf(
                    AccountsTable.COLUMN_EMAIL to signUpData.email,
                    AccountsTable.COLUMN_USERNAME to signUpData.username,
                    AccountsTable.COLUMN_PASSWORD to signUpData.password,
                    AccountsTable.COLUMN_CREATED_AT to System.currentTimeMillis()
                )
            )
        } catch (e: SQLiteConstraintException) {
            val appException = AccountAlreadyExistException()
            appException.initCause(e)
            throw appException
        }

    }

    private fun getAccountById(accountId: Long): Account? {
        if (accountId == AppSettings.NO_ACCOUNT_ID) return null
        val cursor = database.query(
            AccountsTable.TABLE_NAME,
            arrayOf(
                AccountsTable.COLUMN_ID,
                AccountsTable.COLUMN_EMAIL,
                AccountsTable.COLUMN_USERNAME,
                AccountsTable.COLUMN_CREATED_AT
            ),
            "${AccountsTable.COLUMN_ID} = ?",
            arrayOf(accountId.toString()),
            null, null, null
        )
        return cursor.use {
            if (cursor.count == 0) return@use null
            cursor.moveToFirst()
            Account(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_EMAIL)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_CREATED_AT))
            )
        }
    }

    private fun updateUserNameForAccountId(accountId: Long, newUsername: String) {
        database.update(
            AccountsTable.TABLE_NAME,
            contentValuesOf(
                AccountsTable.COLUMN_USERNAME to newUsername
            ),
            "${AccountsTable.COLUMN_ID} = ?",
            arrayOf(accountId.toString())
        )
    }

    private class AccountId(val value: Long)
}
