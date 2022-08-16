package com.mcmouse.nav_tabs

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.SQLiteAccountsRepository
import com.mcmouse.nav_tabs.models.boxes.BoxesRepository
import com.mcmouse.nav_tabs.models.boxes.SQLiteBoxesRepository
import com.mcmouse.nav_tabs.models.settings.AppSettings
import com.mcmouse.nav_tabs.models.settings.SharedPreferencesAppSettings
import com.mcmouse.nav_tabs.models.sqlite.AppSQLiteHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {

    private lateinit var appContext: Context

    /**
     * Чтобы создать базу данных через класс [SQLiteHelper] нужно в его конструктор передать
     * контекст, и вызвать свойсво [writableDatabase] для того, чтобы пользователь мог читать и
     * записывать данные в базу (для того чтобы мог только читать, а не записывать то свойство
     * [readableDatabase])
     */
    private val database: SQLiteDatabase by lazy<SQLiteDatabase> {
        AppSQLiteHelper(appContext).writableDatabase
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    val accountsRepository: AccountsRepository by lazy {
        SQLiteAccountsRepository(database, appSettings, ioDispatcher)
    }

    val boxesRepository: BoxesRepository by lazy {
        SQLiteBoxesRepository(database, accountsRepository, ioDispatcher)
    }

    fun init(context: Context) {
        appContext = context
    }
}