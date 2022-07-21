package com.mcmouse88.choose_color.views

interface UiActions {

    fun showToast(message: String)

    fun getString(messageRes: Int, vararg args: Any): String
}