package com.mcmouse88.foundation.model.tasks

/**
 * Интерфейс для создания имитации задержки
 */
interface ThreadUtils {

    fun sleep(millis: Long)

    class DefaultThread : ThreadUtils {
        override fun sleep(millis: Long) {
            Thread.sleep(millis)
        }
    }
}