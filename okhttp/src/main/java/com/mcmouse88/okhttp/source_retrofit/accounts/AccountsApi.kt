package com.mcmouse88.okhttp.source_retrofit.accounts

import com.mcmouse88.okhttp.source_retrofit.accounts.entities.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AccountsApi {

    @POST("sign-in")
    suspend fun signIn(
        @Body signInRequestEntity: SignInRequestEntity
    ): SignInResponseEntity

    @POST("sign-up")
    suspend fun signUp(@Body body: SignUpRequestEntity)

    @GET("me")
    suspend fun getAccount(): GetAccountResponseEntity

    @PUT("me")
    suspend fun setUserName(@Body body: UpdateUsernameRequestEntity)
}