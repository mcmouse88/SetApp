package com.mcmouse.nav_tabs.models.room

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mcmouse.nav_tabs.Repositories


/**
 * При изменения структуры таблицы, при создании сущности, при помощи которой будет происходить
 * миграция, нужно ее унаследовать от класса [AutoMigrationSpec]. В случае переименования колонок
 * в таблице нужно использовать аннотацию [RenameColumn], в параметрах которой нужно указать,
 * имя таблицы, где произошло переименование колонки, а также старое имя колонки и новое. При
 * переименовании нескольких колонок, используется следующий пример:
 * ```kotlin
 * @RenameColumn.Entries(
 *     value = [
 *         RenameColumn(tableName = "accounts", fromColumnName = "password", toColumnName = "hash"),
 *         RenameColumn(tableName = "accounts", fromColumnName = "created_at", toColumnName = "created_by")
 *     ]
 * )
 * ```
 * Чтобы переписать старые колонки в соотвествии с новыми нововведениями нужно переопределить метод
 * [onPostMigrate()], в котором в формате SQL запроса прописать логику замены данных.
 */
@RenameColumn(tableName = "accounts", fromColumnName = "password", toColumnName = "hash")
class AutoMigrationSpec1To2 : AutoMigrationSpec {

    private val securityUtils = Repositories.securityUtils

    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        super.onPostMigrate(db)
        db.query("SELECT * FROM accounts").use { cursor ->
            val passwordIndex = cursor.getColumnIndex("hash")
            val idIndex = cursor.getColumnIndex("user_id")
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val passwordChars = cursor.getString(passwordIndex).toCharArray()
                val salt = securityUtils.generateSalt()
                val hashByte = securityUtils.passwordToHash(passwordChars, salt)
                db.update(
                    "accounts",
                    SQLiteDatabase.CONFLICT_NONE,
                    contentValuesOf(
                        "hash" to securityUtils.bytesToString(hashByte),
                        "salt" to securityUtils.bytesToString(salt)
                    ),
                    "user_id = ?",
                    arrayOf(id.toString())
                )
            }
        }
    }
}

/**
 * Для осуществления ручной миграции можно создать объект анонимного класса, реализующий анонимный
 * класс [Migration]. В нем нужно переопределить один метод [migrate], в котором нужно прописать
 * SQL запрос, меняющий структуру таблицы.
 *
 */
val MIGRATION_2_3 = object : Migration(7, 8) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE accounts ADD phone_number TEXT")
    }
}