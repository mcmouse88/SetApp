package com.mcmouse88.readwritefiles

open class AppException(
    cause: Throwable? = null
) : RuntimeException(cause)

class CantAccessFileException(
    cause: Throwable
) : AppException(cause)

class ActivityNotStartedException : AppException()

class AlreadyInProgressException : AppException()