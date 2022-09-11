package com.mcmouse88.okhttp.app.model.accounts.entities

import com.mcmouse88.okhttp.app.model.EmptyFieldException
import com.mcmouse88.okhttp.app.model.Field
import com.mcmouse88.okhttp.app.model.PasswordMismatchException

data class SignUpData(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
) {
    fun validate() {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (username.isBlank()) throw EmptyFieldException(Field.Username)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)
        if (password != repeatPassword) throw PasswordMismatchException()
    }
}