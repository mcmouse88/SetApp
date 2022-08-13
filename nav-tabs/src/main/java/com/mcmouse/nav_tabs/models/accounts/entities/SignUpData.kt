package com.mcmouse.nav_tabs.models.accounts.entities

import com.mcmouse.nav_tabs.models.EmptyFieldException
import com.mcmouse.nav_tabs.models.Field
import com.mcmouse.nav_tabs.models.PasswordMismatchException

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
        if (repeatPassword != password) throw PasswordMismatchException()
    }
}