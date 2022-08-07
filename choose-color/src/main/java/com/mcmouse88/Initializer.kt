package com.mcmouse88

import com.mcmouse88.choose_color.model.colors.InMemoryColorsRepository
import com.mcmouse88.foundation.SingletonScopeDependencies
import com.mcmouse88.foundation.model.coroutines.DefaultDispatcher
import com.mcmouse88.foundation.model.coroutines.IoDispatcher
import kotlinx.coroutines.Dispatchers

object Initializer {

    /**
     * Данную функцию будем вызывать во всех точках входа в приложение (в нашем случае только в
     * активити)
     */
    fun initDependencies() {
        SingletonScopeDependencies.init {
            val ioDispatcher = IoDispatcher(Dispatchers.IO)
            val defaultDispatcher = DefaultDispatcher(Dispatchers.Default)

            return@init listOf(
                ioDispatcher,
                defaultDispatcher,

                InMemoryColorsRepository(ioDispatcher)
            )
        }
    }
}