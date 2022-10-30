package com.mcmouse88.okhttp.data.accounts

import com.mcmouse88.okhttp.data.accounts.entities.GetAccountResponseEntity
import com.mcmouse88.okhttp.data.accounts.entities.SignInRequestEntity
import com.mcmouse88.okhttp.data.accounts.entities.SignInResponseEntity
import com.mcmouse88.okhttp.data.accounts.entities.SignUpRequestEntity
import com.mcmouse88.okhttp.data.accounts.entities.UpdateUsernameRequestEntity
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