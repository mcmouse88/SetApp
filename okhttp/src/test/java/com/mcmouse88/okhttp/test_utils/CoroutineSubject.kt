package com.mcmouse88.okhttp.test_utils

import io.mockk.MockKStubScope
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

infix fun <T, B> MockKStubScope<T, B>.returnsSubject(subject: CoroutineSubject<T>) =
    coAnswers { subject.get() }

class CoroutineSubject<T> {

    private val mutex = Mutex(locked = true)
    private lateinit var value: Value<T>

    suspend fun get(): T {
        mutex.lock()
        value.let {
            if (it is Value.Success) return it.data
            if (it is Value.Error) throw it.error
        }
        throw IllegalStateException("Invalid value type")
    }

    fun sendSuccess(data: T) {
        value = Value.Success(data)
        mutex.unlock()
    }

    fun sendError(error: Throwable) {
        value = Value.Error(error)
        mutex.unlock()
    }

    sealed class Value<T> {
        class Error<T>(val error: Throwable) : Value<T>()
        class Success<T>(val data: T) : Value<T>()
    }
}

class AnotherCoroutineSubject<T> {

    private var continuation: Continuation<T>? = null

    suspend fun get(): T = suspendCoroutine { this.continuation = it }

    fun sendError(e: Exception) {
        continuation?.resumeWithException(e)
        continuation = null
    }

    fun sendSuccess(value: T) {
        continuation?.resume(value)
    }
}