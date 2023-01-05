package com.mcmouse88.okhttp.data.base

import com.mcmouse88.okhttp.domain.BackendException
import com.mcmouse88.okhttp.domain.ConnectionException
import com.mcmouse88.okhttp.domain.ParseBackendResponseException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import okio.IOException
import retrofit2.HttpException

open class BaseRetrofitSource(
    retrofitConfig: RetrofitConfig
) {

    private val errorAdapter = retrofitConfig.moshi.adapter(ErrorResponseBody::class.java)

    val retrofit = retrofitConfig.retrofit

    suspend fun<T> wrapRetrofitException(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: JsonDataException) {
            throw ParseBackendResponseException(e)
        } catch (e: JsonEncodingException) {
            throw ParseBackendResponseException(e)
        } catch (e: HttpException) {
            throw createBackendException(e)
        } catch (e: IOException) {
            throw ConnectionException(e)
        }
    }

    private fun createBackendException(e: HttpException): Exception {
        return try {
            val errorBody = checkNotNull(errorAdapter.fromJson(
                e.response()!!.errorBody()!!.string())
            )
            BackendException(e.code(), errorBody.error)
        } catch (exp: Exception) {
            throw ParseBackendResponseException(exp)
        }
    }

    class ErrorResponseBody(
        val error: String
    )
}