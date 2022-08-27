package com.mcmouse88.paging_library.model.users.repositories.room

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mcmouse88.paging_library.model.users.User
import com.mcmouse88.paging_library.model.users.UsersPageLoader
import com.mcmouse88.paging_library.model.users.UsersPagingSource
import com.mcmouse88.paging_library.model.users.repositories.UsersRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class RoomUsersRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val usersDao: UsersDao
) : UsersRepository {

    private val enableErrorsFlow = MutableStateFlow(false)

    override fun isErrorEnabled(): Flow<Boolean> = enableErrorsFlow

    override fun setErrorEnabled(value: Boolean) {
        enableErrorsFlow.value = value
    }

    override fun getPagedUsers(searchBy: String): Flow<PagingData<User>> {
        val loader: UsersPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, searchBy)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UsersPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    private suspend fun getUsers(pageIndex: Int, pageSize: Int, searchBy: String): List<User>
    = withContext(ioDispatcher) {
        delay(2_000)
        if (enableErrorsFlow.value) throw IllegalStateException("Error!")

        val offset = pageIndex * pageSize
        val list = usersDao.getUsers(pageSize, pageIndex, searchBy)

        return@withContext list.map(UserDbEntity::toUser)
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}