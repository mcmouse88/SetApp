package com.mcmouse88.okhttp.source.base

import com.google.gson.reflect.TypeToken
import com.mcmouse88.okhttp.app.model.BackendException
import com.mcmouse88.okhttp.app.model.ConnectionException
import com.mcmouse88.okhttp.app.model.ParseBackendResponseException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class BaseOkHttpSource(
    private val config: OkHttpConfig
) {
    private val contentType = "application/json; charset=utf-8".toMediaType()

    val gson = config.gson
    val client = config.client



    /**
     * Так как клиенты [OkHttpClient] не поддерживают работу с корутинами, то есть у них нет
     * suspend функций. Поэтому напишем свою suspend функцию, которая будет расширять возможности
     * класса [OkHttpClient], и оборачивать сетевые запросы в suspend функцию. Для того, чтобы
     * превратить обычные калбэки и слушатели в корутину нужно использовать специальный метод
     * [suspendCancellableCoroutine]. Данный метод будет extension методом класса [Call], который
     * является частью библиотеки OkHttp3, и он отвечает за единоразовое выполнение какого-либо
     * запроса. У класса [Call] есть два метода, один из них [execute], который является синхронным
     * и блокирует текущий поток на котором был вызван, и метод [enqueue], который является
     * асинхронным и в него передается слушатель результата запроса. Также у класса [Call] есть
     * метод [cancel], который отменяет текущий запрос.
     */
    suspend fun Call.suspendEnqueue(): Response {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }
            enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    val appException = ConnectionException(e)
                    continuation.resumeWithException(appException)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        continuation.resume(response)
                    } else {
                        handleErrorResponse(response, continuation)
                    }
                }
            })
        }
    }

    fun Request.Builder.endpoint(endpoint: String): Request.Builder {
        url("${config.baseUrl}$endpoint")
        return this
    }

    fun<T> T.toJsonRequestBody(): RequestBody {
        val json = gson.toJson(this)
        return json.toRequestBody(contentType)
    }

    /**
     * Два метода для преобразования данных, метод с параметром [TypeToken] предназначен для
     * парсинга массивов, а без него для объектов.
     */
    fun<T> Response.parseJsonResponse(typeToken: TypeToken<T>): T {
        try {
            return gson.fromJson(this.body!!.string(), typeToken.type)
        } catch (e: Exception) {
            throw ParseBackendResponseException(e)
        }
    }

    inline fun<reified T> Response.parseJsonResponse(): T {
        try {
            return gson.fromJson(this.body!!.string(), T::class.java)
        } catch (e: Exception) {
            throw ParseBackendResponseException(e)
        }
    }

    /**
     * Метод для обработки и получения информации об ошибках от сервера. Можно для записи
     * данных об ошибках использовать data class, но для примера используем сущность [Map]
     */
    private fun handleErrorResponse(
        response: Response,
        continuation: CancellableContinuation<Response>
    ) {
        val httpCode = response.code
        try {
            val map = gson.fromJson(response.body!!.string(), Map::class.java)
            val message = map["error"].toString()
            continuation.resumeWithException(BackendException(httpCode, message))
        } catch (e: Exception) {
            val appException = ParseBackendResponseException(e)
            continuation.resumeWithException(appException)
        }
    }
 }