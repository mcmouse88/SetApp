package com.mcmouse.nav_tabs.models.boxes.room

import androidx.room.*
import com.mcmouse.nav_tabs.models.boxes.room.entities.AccountBoxSettingDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.entities.BoxAndSettingAndTuple
import com.mcmouse.nav_tabs.models.boxes.room.entities.BoxDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.views.SettingsDbView
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    /**
     * Чтобы получить данные из связанных таблиц лучше использовать сущность [Map], в которой
     * ключом будет сущность родительской таблицы, а значением сущность зависимой таблицы.
     */
    /*@Query("SELECT * " +
            "FROM boxes " +
            "LEFT JOIN accounts_boxes_settings " +
            "ON boxes.box_id = accounts_boxes_settings.box_user_id " +
            "AND accounts_boxes_settings.account_id = :accountId")
    fun getBoxesAndSettings(accountId: Long): Flow<Map<BoxDbEntity, AccountBoxSettingDbEntity?>>*/

    /**
     * Вместо маппы также можно использовать Tuple с embedded полями.
     */
    /*@Query("SELECT * " +
            "FROM boxes " +
            "LEFT JOIN accounts_boxes_settings " +
            "ON boxes.box_id = accounts_boxes_settings.box_user_id " +
            "AND accounts_boxes_settings.account_id = :accountId")
    fun getBoxesAndSettings(accountId: Long): Flow<List<BoxAndSettingAndTuple>>*/

    /**
     * Вариант того же запроса с использованием [DatabaseView]
     */
    @Query("SELECT * FROM settings_view WHERE account_id = :accountId")
    fun getBoxesAndSettings(accountId: Long): Flow<List<SettingsDbView>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveFlagForBox(accountBoxSettingDbEntity: AccountBoxSettingDbEntity)
}