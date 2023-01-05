package com.mcmouse88.remote_mediator.data.retrofit

import com.google.gson.annotations.SerializedName
import com.mcmouse88.remote_mediator.domain.Launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class LaunchNetworkEntity(
    @SerializedName("flight_number") override val id: Long,
    override val name: String,
    val details: String?,
    val links: Links?,
    val dateUnix: Long,
    val success: Boolean
) : Launch {

    override val description: String get() = details ?: "-"
    override val isSuccess: Boolean get() = success
    override val imageUrl: String get() = links?.patch?.small ?: ""
    override val launchTimeStamp: Long get() = dateUnix

    override val year: Int get() = Calendar.getInstance().apply {
        timeInMillis = TimeUnit.SECONDS.toMillis(launchTimeStamp)
    }.get(Calendar.YEAR)
}

data class Links(
    val patch: Images?
)

data class Images(
    val small: String?
)