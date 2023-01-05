package com.mcmouse.nav_tabs.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AsyncLoader<T>(
    private val loader: suspend () -> T
) {
    private val mutex = Mutex()
    private var value: T? = null

    suspend fun get(): T {
        mutex.withLock {
            if (value == null) {
                value = loader()
            }
        }
        return value ?: throw IllegalStateException("Failed initialize value")
    }
}