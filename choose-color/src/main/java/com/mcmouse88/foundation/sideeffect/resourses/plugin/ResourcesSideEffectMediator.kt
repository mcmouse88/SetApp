package com.mcmouse88.foundation.sideeffect.resourses.plugin

import android.content.Context
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.resourses.Resources

class ResourcesSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Resources {

    override fun getString(resIdRes: Int, vararg args: Any): String {
        return appContext.getString(resIdRes, *args)
    }
}