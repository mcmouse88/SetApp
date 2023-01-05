package com.mcmouse88.remote_mediator.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.mcmouse88.remote_mediator.data.retrofit.LaunchesApi
import com.mcmouse88.remote_mediator.data.retrofit.LaunchesQuery
import com.mcmouse88.remote_mediator.data.room.LaunchRoomEntity
import com.mcmouse88.remote_mediator.data.room.LaunchesDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * [RemoteMediator] используется для того, чтобы реализовать не просто список с пагинацией, а
 * список с пагинацией и кэширование данных в локальное хранилище. для того, чтобы использовать
 * [RemoteMediator] нужно создать свой класс, который будет наследоваться от класса [RemoteMediator].
 * Сам [RemoteMediator] принимает два типа, тип ключа для загрузки страницы и тип данных, который
 * нужно сохранить в локальное хранилище. В конструктор созданный класс принимает в качестве
 * параметра источник данных из локальной базы, это может быть как объект базы данных(AppDatabase),
 * так и как в нашем случае интерфейс Dao. Вторая зависимость это источник данных из сети. Также
 * в созданном классе необходимо реализовать метод [load]. Этот метод должен уметь загружать данные
 * из сети и сохранять в базу.
 */
@OptIn(ExperimentalPagingApi::class)
class LaunchesRemoteMediator @AssistedInject constructor(
    private val launchesDao: LaunchesDao,
    private val launchesApi: LaunchesApi,
    @Assisted private val year: Int?
) : RemoteMediator<Int, LaunchRoomEntity>() {

    private var pageIndex = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LaunchRoomEntity>
    ): MediatorResult {
        pageIndex =
            getPageIndex(loadType) ?: return MediatorResult.Success(endOfPaginationReached = true)

        val limit = state.config.pageSize
        val offset = pageIndex * limit

        return try {
            val launches = fetchLaunches(limit, offset)
            if (loadType == LoadType.REFRESH) {
                launchesDao.refresh(year, launches)
            } else {
                launchesDao.save(launches)
            }
            MediatorResult.Success(
                endOfPaginationReached = launches.size < limit
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    /**
     * В этом методе реализован рассчет страницы для загрузки. Если [loadType] равен значению
     * [LoadType.REFRESH] то список нужно полностью обновить, если значению [LoadType.PREPEND] то
     * возвращаем null так как наже приложение не поддерживает обратную пагинацию, а если
     * [LoadType.APPEND] то увеличиваем индекс текущий страницы на единицу.
     */
    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    private suspend fun fetchLaunches(
        limit: Int,
        offset: Int
    ): List<LaunchRoomEntity> {
        val query = LaunchesQuery.create(
            year = year,
            limit = limit,
            offset = offset
        )
        return launchesApi.getLaunches(query)
            .docs
            .map { LaunchRoomEntity(it) }
    }

    @AssistedFactory
    interface Factory {
        fun create(year: Int?): LaunchesRemoteMediator
    }
}