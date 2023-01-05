package com.mcmouse88.remote_mediator.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mcmouse88.remote_mediator.domain.Launch

@Entity(tableName = "launches")
data class LaunchRoomEntity(
    @PrimaryKey override val id: Long,
    override val name: String,
    override val description: String,
    override val imageUrl: String,
    override val year: Int,
    @ColumnInfo(index = true) override val launchTimeStamp: Long,
    override val isSuccess: Boolean
) : Launch {

    constructor(launch: Launch) : this(
        id = launch.id,
        name = launch.name,
        description = launch.description,
        imageUrl= launch.imageUrl,
        year = launch.year,
        launchTimeStamp = launch.launchTimeStamp,
        isSuccess = launch.isSuccess
    )
}