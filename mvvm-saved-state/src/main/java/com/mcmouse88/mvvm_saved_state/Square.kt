package com.mcmouse88.mvvm_saved_state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Square private constructor(
    val size: Int,
    val color: List<Int>
) : Parcelable {
    constructor(
        size: Int,
        colorProducer: () -> Int
    ) : this(
        size = size,
        color = (0 until size * size).map { colorProducer() }
    )
}