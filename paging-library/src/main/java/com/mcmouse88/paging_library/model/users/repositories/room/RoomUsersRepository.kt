package com.mcmouse88.paging_library.model.users.repositories.room

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
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

    /**
     * При создании класса Pager (который мы возвращаем при запросе данных), нам нужно передать
     * следующие параметры, это config - класс [PagingConfig], в который передаем размер страницыб
     * и нужно ли включать отображение placeHolder (для того, чтобы отображать его вместо элементов
     * списка [RecyclerView] если данные еще не успели загрузиться, но тогда нужно учитывать, что
     * адаптеры должны уметь работать со значениями типа null. Также можно передать initialLoadSize,
     * это количество элементов при стартовой загрузке, которое по умолчание равно pageSize * 3. А
     * также нужно передать парметр [pagingSourceFactory], который создаст объект типа
     * [PagingSource], в котором мы прописывали логику загрузки и обновления загрузки данных.
     */
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