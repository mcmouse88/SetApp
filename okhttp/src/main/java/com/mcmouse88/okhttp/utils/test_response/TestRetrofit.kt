package com.mcmouse88.okhttp.utils.test_response

import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class SignInRequestBodyRetrofit(
    val email: String,
    val password: String
)

data class SignInResponseBodyRetrofit(
    val token: String
)

interface Api {
    @POST("sign-in")
    suspend fun signIn(@Body body: SignInRequestBodyRetrofit): SignInResponseBody
}

fun main() = runBlocking {
    val loggerInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
        .addInterceptor(loggerInterceptor)
        .build()
    val moshi = Moshi.Builder().build()
    val moshiConverterFactory = MoshiConverterFactory.create(moshi)
    val retrofit = Retrofit.Builder()
        .baseUrl("http://172.19.0.1:12345")
        .client(client)
        .addConverterFactory(moshiConverterFactory)
        .build()

    val api = retrofit.create(Api::class.java)
    val requestBody = SignInRequestBodyRetrofit(
        email = "admin@google.com",
        password = "123"
    )

    val response = api.signIn(requestBody)

    println("TOKEN = ${response.token}")
}