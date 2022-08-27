package com.mcmouse88.paging_library

import android.content.Context
import androidx.room.Room
import com.mcmouse88.paging_library.model.AppDataBase
import com.mcmouse88.paging_library.model.users.repositories.UsersRepository
import com.mcmouse88.paging_library.model.users.repositories.room.RoomUsersRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {

    private lateinit var appContext: Context

    private val database: AppDataBase by lazy {
        Room.databaseBuilder(appContext, AppDataBase::class.java, "database.db")
            .createFromAsset("initial_database.db")
            .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    val usersRepository: UsersRepository by lazy {
        RoomUsersRepository(ioDispatcher, database.getUsersDao())
    }

    fun init(context: Context) {
        appContext = context
    }
}