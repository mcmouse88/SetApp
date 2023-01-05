package com.mcmouse88.okhttp.domain

open class AppException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
}

class EmptyFieldException(
    val field: Field
) : AppException()

class PasswordMismatchException : AppException()

class AccountAlreadyExistException(
    cause: Throwable
) : AppException(cause)

class AuthException(
    cause: Throwable
) : AppException(cause)

class InvalidCredentialsException(
    cause: Throwable
) : AppException(cause)

class ConnectionException(
    cause: Throwable
) : AppException(cause)

open class BackendException(
    val code: Int,
    message: String
) : AppException(message)

class ParseBackendResponseException(
    cause: Throwable
) : AppException(cause)

internal suspend fun <T> wrapBackendException(block: suspend () -> T): T {
    try {
        return block.invoke()
    } catch (e: BackendException) {
        if (e.code == 401) throw AuthException(e)
        else throw e
    }
}