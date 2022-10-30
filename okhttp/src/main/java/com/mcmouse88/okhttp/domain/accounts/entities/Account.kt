package com.mcmouse88.okhttp.domain.accounts.entities

data class Account(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: Long = UNKNOWN_CREATE_AT
) {
    companion object {
        const val UNKNOWN_CREATE_AT = 0L
    }
}