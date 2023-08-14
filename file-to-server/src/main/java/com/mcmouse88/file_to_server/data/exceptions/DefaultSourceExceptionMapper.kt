package com.mcmouse88.file_to_server.data.exceptions

import com.mcmouse88.file_to_server.domain.AppException
import com.mcmouse88.file_to_server.domain.AuthException
import com.mcmouse88.file_to_server.domain.ConnectionException
import com.mcmouse88.file_to_server.domain.InternalException
import okio.IOException
import retrofit2.HttpException

class DefaultSourceExceptionMapper : SourceExceptionMapper {

    override fun mapException(exception: Throwable): AppException {
        when(exception) {
            is HttpException -> {
                if (exception.code() == 401) return AuthException()
                return InternalException(exception)
            }
            is IOException -> return ConnectionException(exception)
            else -> return InternalException(exception)
        }
    }
}