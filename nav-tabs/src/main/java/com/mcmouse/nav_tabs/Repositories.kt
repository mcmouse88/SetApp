package com.mcmouse.nav_tabs

import android.content.Context
import androidx.room.Room
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.room.RoomAccountsRepository
import com.mcmouse.nav_tabs.models.boxes.BoxesRepository
import com.mcmouse.nav_tabs.models.boxes.room.RoomBoxesRepository
import com.mcmouse.nav_tabs.models.room.AppDataBase
import com.mcmouse.nav_tabs.models.settings.AppSettings
import com.mcmouse.nav_tabs.models.settings.SharedPreferencesAppSettings
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
    private val database: AppDataBase by lazy {
        Room.databaseBuilder(appContext, AppDataBase::class.java, "database.db")
            .createFromAsset("initial_database.db")
            .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    val accountsRepository: AccountsRepository by lazy {
        RoomAccountsRepository(database.getAccountsDao(), appSettings, ioDispatcher)
    }

    val boxesRepository: BoxesRepository by lazy {
        RoomBoxesRepository(database.getBoxesDao()  , accountsRepository, ioDispatcher)
    }

    fun init(context: Context) {
        appContext = context
    }
}