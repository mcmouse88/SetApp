package com.mcmouse88.remote_mediator.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface LaunchesRepository {

    fun getLaunches(year: Int? = null): Flow<PagingData<Launch>>

    suspend fun toggleSuccessFlag(launch: Launch)
}