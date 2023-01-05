package com.mcmouse.nav_tabs.models.boxes.room.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded

/**
 * Если нужно сделать какой-то большой запрос из базы данных и объеденить данные из нескольких
 * таблиц, то можно использовать сущность [DatabaseView], в параметрах которого нужно указать
 * имя сущности (любое значение типа String), и прописать сам запрос. Также обязательно
 * [DatabaseView] нужно указать в аннотации класса базы данных.
 */
@DatabaseView(
    viewName = "settings_view",
    value = "SELECT accounts.user_id AS account_id,\n" +
            " boxes.box_id AS box_user_id,\n" +
            " ifnull(accounts_boxes_settings.is_active, 1) AS is_active\n" +
            "FROM accounts\n" +
            "JOIN boxes\n" +
            "LEFT JOIN accounts_boxes_settings\n" +
            "ON accounts_boxes_settings.account_id = accounts.user_id\n" +
            "AND accounts_boxes_settings.box_user_id = boxes.box_id"
)
data class SettingsDbView(
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "box_user_id") val boxUserId: Long,
    @Embedded val settings: SettingsTuple
) {
}