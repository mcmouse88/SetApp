package com.mcmouse88.remote_mediator.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LaunchesDao {

    /**
     * Для работы с [RemoteMediator] нужен метод получения пагинированного списка, и для фильтра
     * по годам, функция опционально принимает значение года в качестве параметра. Здесь мы уже не
     * реализовываем [PagingSource] сами, так как это и так уже умеет делать бибилотека Room, для
     * этого нужно подключить дополнительную зависимость:
     * ```kotlin
     * implementation "androidx.room:room-paging:$room_version"
     * ```
     *
     */
    @Query("SELECT * FROM launches WHERE :year IS NULL OR year = :year ORDER BY launchTimeStamp DESC")
    fun getPagingSource(
        year: Int?
    ): PagingSource<Int, LaunchRoomEntity>

    /**
     * Также нужен метод для сохранения списка в базу данных. Он используется для того, чтобы при
     * получении списка из сети мы могли сохранить его в базу.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(launches: List<LaunchRoomEntity>)

    /**
     * Также есть метод для удаления данных из базы, и опционально имеется параметра для фильтра
     */
    @Query("DELETE FROM launches WHERE :year IS NULL OR year = :year")
    suspend fun clear(year: Int?)

    /**
     * Также есть метод [Transaction] для замены старых данных на новые. А именно, если данные
     * поменялись или были удалены с сервера.
     */
    @Transaction
    suspend fun refresh(year: Int?, launches: List<LaunchRoomEntity>) {
        clear(year)
        save(launches)
    }

    suspend fun save(launch: LaunchRoomEntity) {
        save(listOf(launch))
    }
}