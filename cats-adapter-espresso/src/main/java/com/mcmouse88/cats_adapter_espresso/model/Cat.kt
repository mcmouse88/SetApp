package com.mcmouse88.cats_adapter_espresso.model

data class Cat(
    val id: Long,
    val name: String,
    val photoUrl: String,
    val description: String,
    val isFavorite: Boolean
)