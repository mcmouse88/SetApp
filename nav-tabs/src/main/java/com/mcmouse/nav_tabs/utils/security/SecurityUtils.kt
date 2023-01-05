package com.mcmouse.nav_tabs.utils.security

interface SecurityUtils {

    fun generateSalt(): ByteArray

    fun passwordToHash(password: CharArray, salt: ByteArray): ByteArray

    fun bytesToString(bytes: ByteArray): String

    fun stringToByte(string: String): ByteArray
}