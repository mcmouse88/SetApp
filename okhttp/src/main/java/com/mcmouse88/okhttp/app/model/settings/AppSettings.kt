package com.mcmouse88.okhttp.app.model.settings

interface AppSettings {

    fun getCurrentToken(): String?

    fun setCurrentToken(token: String?)
}