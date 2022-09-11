package com.mcmouse88.okhttp.app.model

sealed class ResultResponse<T> {

    fun<R> mapResult(mapper: ((T) -> R)? = null): ResultResponse<R> {
        return when (this) {
            is Success<T> -> {
                if (mapper == null) {
                    throw IllegalStateException("Can't map Success<T> result without mapper.")
                } else {
                    Success(mapper(this.value))
                }
            }
            is Error<T> -> Error(this.error)
            is Empty<T> -> Empty()
            is Pending<T> -> Pending()
        }
    }

    fun getValueOrNull(): T? {
        if (this is Success<T>) return this.value
        return null
    }

    fun isFinished() = this is Success<T> || this is Error<T>
}

class Success<T>(
    val value: T
) : ResultResponse<T>()

class Error<T>(
    val error: Throwable
) : ResultResponse<T>()

class Empty<T> : ResultResponse<T>()

class Pending<T> : ResultResponse<T>()