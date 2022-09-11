package com.mcmouse88.okhttp.app.utiils.logger

interface Logger {

    fun log(tag: String, message: String)

    fun error(tag: String, e: Throwable)
}