package com.mcmouse.nav_tabs.models.boxes.room.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.entities.AccountBoxSettingDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.entities.BoxDbEntity

data class SettingsTuple(
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

/**
 * для того чтобы объединять данные из таблиц используется аннотация [Embedded]
 */
data class BoxAndSettingAndTuple(
    @Embedded val boxDbEntity: BoxDbEntity,
    @Embedded val settingDbEntity: AccountBoxSettingDbEntity?
)

/**
 * Также для комплексного запроса из реляционных таблиц можно использовать аннотацию [Relation].
 * Для этого нужно создать дата класс, который будет содержать поля [DatabaseView], и две [Entity],
 * связанные между собой [DatabaseView]. В качестве параметров в аннотацию [Relation] передается
 * parentColumn, который содержит внешний ключ идентификатора, находящийся в [DatabaseView], и
 * entityColumn, первичный ключ сущности [Entity].
 */
data class SettingWithEntitiesTuple(
    @Embedded val settingDbEntities: SettingsDbView,

    @Relation(
        parentColumn = "account_id",
        entityColumn = "user_id"
    )
    val accountDbEntity: AccountDbEntity,
    @Relation(
        parentColumn = "box_user_id",
        entityColumn = "box_id"
    )
    val boxDbEntity: BoxDbEntity
)