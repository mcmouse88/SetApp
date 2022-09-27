package com.mcmouse88.remote_mediator.domain

interface Launch {
    val id: Long
    val name: String
    val description: String
    val imageUrl: String
    val year: Int
    val launchTimeStamp: Long
    val isSuccess: Boolean
}