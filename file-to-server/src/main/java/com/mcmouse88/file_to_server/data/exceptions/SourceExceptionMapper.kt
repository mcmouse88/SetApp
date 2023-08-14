package com.mcmouse88.file_to_server.data.exceptions

import com.mcmouse88.file_to_server.domain.AppException

interface SourceExceptionMapper {

    fun mapException(exception: Throwable): AppException

    suspend fun <T> wrap(block: suspend () -> T): T {
        try {
            return block()
        } catch (e: Throwable) {
            if (e is AppException) throw e else throw mapException(e)
        }
    }
}