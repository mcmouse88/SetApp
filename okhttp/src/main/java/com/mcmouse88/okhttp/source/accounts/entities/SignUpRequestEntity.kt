package com.mcmouse88.okhttp.source.accounts.entities

data class SignUpRequestEntity(
    val email: String,
    val username: String,
    val password: String
)