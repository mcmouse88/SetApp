package com.mcmouse88.box_quiz

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Options(
    val boxCount: Int,
    val isTimerEnabled: Boolean
) : Parcelable {

    companion object{
        val DEFAULT = Options(boxCount = 3, isTimerEnabled = false)
    }
}