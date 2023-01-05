package com.mcmouse88.okhttp.data.accounts.entities

import com.mcmouse88.okhttp.domain.accounts.entities.Account

data class GetAccountResponseEntity(
    val id: Long,
    val email: String,
    val username: String,
    val createdAt: Long
) {

    fun toAccount(): Account = Account(
        id = id,
        username = username,
        email = email,
        createdAt = createdAt
    )
}