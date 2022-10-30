package com.mcmouse88.okhttp.domain.settings

interface AppSettings {

    fun getCurrentToken(): String?

    fun setCurrentToken(token: String?)
}