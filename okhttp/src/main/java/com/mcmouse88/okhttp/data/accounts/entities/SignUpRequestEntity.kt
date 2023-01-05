package com.mcmouse88.okhttp.data.accounts.entities

data class SignUpRequestEntity(
    val email: String,
    val username: String,
    val password: String
)