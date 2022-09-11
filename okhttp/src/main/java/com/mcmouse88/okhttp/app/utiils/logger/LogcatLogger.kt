package com.mcmouse88.okhttp.app.utiils.logger

import android.util.Log

object LogcatLogger : Logger {

    override fun log(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun error(tag: String, e: Throwable) {
        Log.e(tag, "Error!", e)
    }
}