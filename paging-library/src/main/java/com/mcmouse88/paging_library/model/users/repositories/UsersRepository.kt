package com.mcmouse88.paging_library.model.users.repositories

import androidx.paging.PagingData
import com.mcmouse88.paging_library.model.users.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    fun isErrorEnabled(): Flow<Boolean>

    fun setErrorEnabled(value: Boolean)

    fun getPagedUsers(searchBy: String): Flow<PagingData<User>>

    suspend fun setIsFavorite(user: User, isFavorite: Boolean)

    suspend fun delete(user: User)
}