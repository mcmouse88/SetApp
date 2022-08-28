package com.mcmouse88.paging_library.model.users.repositories.room

import androidx.room.Dao
import androidx.room.Query
import androidx.viewpager2.widget.ViewPager2

@Dao
interface UsersDao {

    /**
     * В качестве источника данных в проекте выступает база данных. Данная функция отдает данные, и
     * имеет параметры для пагинации, и фильтрации данных при поиске.
     */
    @Query("SELECT * FROM users " +
    "WHERE :searchBy = '' OR name LIKE '%' || :searchBy || '%' " +
    "ORDER BY name " +
    "LIMIT :limit OFFSET :offset")
    suspend fun getUsers(limit: Int, offset: Int, searchBy: String = ""): List<UserDbEntity>
}