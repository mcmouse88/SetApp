package com.mcmouse88.foundation.sideeffect.resourses

import androidx.annotation.StringRes

interface Resources {

    fun getString(@StringRes resIdRes: Int, vararg args: Any): String
}