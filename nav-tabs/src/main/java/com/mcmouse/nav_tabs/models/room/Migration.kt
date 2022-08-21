package com.mcmouse.nav_tabs.models.room

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec


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
 */
@RenameColumn(tableName = "accounts", fromColumnName = "password", toColumnName = "hash")
class AutoMigrationSpec1To2 : AutoMigrationSpec {

}