package com.mcmouse.nav_tabs.models.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mcmouse.nav_tabs.models.accounts.room.AccountsDao
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.BoxesDao
import com.mcmouse.nav_tabs.models.boxes.room.entities.AccountBoxSettingDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.entities.BoxDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.views.SettingsDbView

/**
 * При реализации автомиграции, в аннотации [Database] нужно добавить свойство [autoMigrations],
 * в котором нужно в качестве параметров указать с какой версии на какую минрировать, а также
 * класс spec, при помощи которого юудет организована миграция.
 */
@Database(
    version = 6,
    entities = [AccountDbEntity::class, BoxDbEntity::class, AccountBoxSettingDbEntity::class],
    views = [SettingsDbView::class],
    autoMigrations = [AutoMigration(
        from = 5,
        to = 6,
        spec = AutoMigrationSpec1To2::class
    )]
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getAccountsDao(): AccountsDao

    abstract fun getBoxesDao(): BoxesDao
}