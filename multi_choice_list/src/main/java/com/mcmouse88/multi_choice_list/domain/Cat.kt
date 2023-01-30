package com.mcmouse88.multi_choice_list.domain

data class Cat(
    val id: Long,
    val name: String,
    val photoUrl: String,
    val description: String,
    val isFavorite: Boolean
)