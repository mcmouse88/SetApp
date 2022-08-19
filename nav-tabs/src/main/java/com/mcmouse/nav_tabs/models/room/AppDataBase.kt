package com.mcmouse.nav_tabs.models.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mcmouse.nav_tabs.models.accounts.room.AccountsDao
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.BoxesDao
import com.mcmouse.nav_tabs.models.boxes.room.entities.AccountBoxSettingDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.entities.BoxDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.views.SettingsDbView

@Database(
    version = 4,
    entities = [AccountDbEntity::class, BoxDbEntity::class, AccountBoxSettingDbEntity::class],
    views = [SettingsDbView::class]
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getAccountsDao(): AccountsDao

    abstract fun getBoxesDao(): BoxesDao
}