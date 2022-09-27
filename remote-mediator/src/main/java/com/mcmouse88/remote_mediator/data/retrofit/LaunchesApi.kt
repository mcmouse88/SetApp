package com.mcmouse88.remote_mediator.data.retrofit

import retrofit2.http.Body
import retrofit2.http.POST

interface LaunchesApi {

    @POST("launches/query")
    suspend fun getLaunches(
        @Body launchesQuery: LaunchesQuery
    ): LaunchesResponse
}