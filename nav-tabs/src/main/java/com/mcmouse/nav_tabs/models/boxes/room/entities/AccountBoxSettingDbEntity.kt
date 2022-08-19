package com.mcmouse.nav_tabs.models.boxes.room.entities

import androidx.room.*
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountDbEntity

/**
 * Чтобы объявить составной первичный ключ (из двух и более колонок), нужно в аннотации [Entity]
 * прописать значение primaryKeys и в массиве перечислить название колонок (именно колонок таблицы).
 * Так как составной первичный ключ будет эффективно искать только по первой колонке, указанной в
 * массиве, то если нам нужно будет искать какие-либо данные по второму (и последующим) полю
 * первичного ключа, то нужно его добавить в индекс. Чтобы создать внешние ключи, то внутри
 * аннотации [Entity] прописывается аннотация [ForeignKey], которая в качестве параметра
 * принимает класс entity, с таблицой которого будет связь, а также parentColumns (колонку
 * класса Entity с которым будет связь), и childColumns (колонку класса содержащего внешний ключ).
 * Также опционально можно добавить параметры поведения в случае удаления или обновления колонок
 * с которыми связана таблица внешним ключом, это параметры onDelete и onUpdate.
 */
@Entity(
    tableName = "accounts_boxes_settings",
    primaryKeys = ["account_id", "box_user_id"],
    indices = [Index("box_user_id")],
    foreignKeys = [
        ForeignKey(
            entity = AccountDbEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BoxDbEntity::class,
            parentColumns = ["box_id"],
            childColumns = ["box_user_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)

/**
 * Также классы Tuple можно добавлять в поля класса [Entity], в таком случае он помечается
 * аннотацией [Embedded], в данном случае это больше для примера, и лучше использовать это когда
 * в таблице много колонок, одно поле в отдельный класс выносить смысла нет.
 */
data class AccountBoxSettingDbEntity(
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "box_user_id") val boxUserId: Long,
    @Embedded val setting: SettingsTuple
)