package com.mcmouse88.remote_mediator.ui

import com.mcmouse88.remote_mediator.domain.Launch

data class LaunchUiEntity(
    override val id: Long,
    override val name: String,
    override val description: String,
    override val imageUrl: String,
    override val year: Int,
    override val launchTimeStamp: Long,
    override val isSuccess: Boolean,
    val isChecked: Boolean
) : Launch {

    constructor(launch: Launch, isChecked: Boolean) : this(
        id = launch.id,
        name = launch.name,
        description = launch.description,
        imageUrl = launch.imageUrl,
        year = launch.year,
        launchTimeStamp = launch.launchTimeStamp,
        isSuccess = launch.isSuccess,
        isChecked = isChecked
    )
}