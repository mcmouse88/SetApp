package com.mcmouse.nav_tabs.models

open class AppException : RuntimeException()

class EmptyFieldException(
    val field: Field
) : AppException()

class PasswordMismatchException : AppException()

class AccountAlreadyExistException : AppException()

class AuthException : AppException()