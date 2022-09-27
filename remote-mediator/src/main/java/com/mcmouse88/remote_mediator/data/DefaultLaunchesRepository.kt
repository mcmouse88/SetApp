package com.mcmouse88.remote_mediator.data

import androidx.paging.*
import com.mcmouse88.remote_mediator.data.room.LaunchRoomEntity
import com.mcmouse88.remote_mediator.data.room.LaunchesDao
import com.mcmouse88.remote_mediator.domain.Launch
import com.mcmouse88.remote_mediator.domain.LaunchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Для того, чтобы использовать [RemoteMediator] в класс [Pager] необходимо добавить дополнительный
 * параметр [remoteMediator], где нужно создать экземпляр нашего класса [RemoteMediator]
 */
@Singleton
class DefaultLaunchesRepository @Inject constructor(
    private val launchesDao: LaunchesDao,
    private val remoteMediatorFactory: LaunchesRemoteMediator.Factory
) : LaunchesRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getLaunches(year: Int?): Flow<PagingData<Launch>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = remoteMediatorFactory.create(year),
            pagingSourceFactory = { launchesDao.getPagingSource(year) }
        ).flow
            .map { pagingData ->
                pagingData.map { launchRoomEntity ->
                    launchRoomEntity
                }
            }
    }

    override suspend fun toggleSuccessFlag(launch: Launch) {

        val editedEntity = LaunchRoomEntity(launch)
            .copy(isSuccess = launch.isSuccess.not())
        launchesDao.save(editedEntity)
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}