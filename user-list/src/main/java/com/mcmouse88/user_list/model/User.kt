package com.mcmouse88.user_list.model

data class User(
    val id: Long,
    val photo: String,
    val name: String,
    val company: String
)

data class UserDetail(
    val user: User,
    val detail: String
)