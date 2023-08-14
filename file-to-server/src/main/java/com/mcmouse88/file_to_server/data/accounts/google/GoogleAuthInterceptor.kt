package com.mcmouse88.file_to_server.data.accounts.google

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class GoogleAuthInterceptor(
    private val context: Context
) : Interceptor {

    private var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken()
        if (token != null) {
            val response = executeWithToken(token, chain)
            if (response.code == 401) {
                val newToken = getGoogleAccessToken(context)
                this.token = newToken
                if (newToken != null) {
                    return executeWithToken(newToken, chain)
                }
            } else {
                return response
            }
        }
        return chain.proceed(chain.request())
    }

    private fun executeWithToken(token: String, chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }

    private fun getToken(): String? {
        if (token == null) {
            token = getGoogleAccessToken(context)
        }
        return token
    }
}