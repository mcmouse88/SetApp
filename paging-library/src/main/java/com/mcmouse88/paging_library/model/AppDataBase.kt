package com.mcmouse88.paging_library.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mcmouse88.paging_library.model.users.repositories.room.UserDbEntity
import com.mcmouse88.paging_library.model.users.repositories.room.UsersDao

@Database(
    version = 1,
    entities = [UserDbEntity::class]
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getUsersDao(): UsersDao
}