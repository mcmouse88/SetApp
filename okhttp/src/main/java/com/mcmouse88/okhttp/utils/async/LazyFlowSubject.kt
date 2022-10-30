package com.mcmouse88.okhttp.utils.async

import com.mcmouse88.okhttp.domain.ResultResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking

typealias SuspendValueLoader<A, T> = suspend (A) -> T?

class LazyFlowSubject<A : Any, T : Any>(
    private val loader: SuspendValueLoader<A, T>
) {

    private val lazyListenersSubject = LazyListenersSubject<A, T> { arg ->
        runBlocking {
            loader.invoke(arg)
        }
    }

    fun reloadAll(silentMode: Boolean = false) {
        lazyListenersSubject.reloadAll(silentMode)
    }

    fun reloadArgument(argument: A, silentMode: Boolean = false) {
        lazyListenersSubject.reloadArgument(argument, silentMode)
    }

    fun updateAllValues(newValue: T?) {
        lazyListenersSubject.updateAllValues(newValue)
    }

    fun listen(argument: A): Flow<ResultResponse<T>> = callbackFlow {
        val listener: ValueListener<T> = { result ->
            trySend(result)
        }
        lazyListenersSubject.addListener(argument, listener)
        awaitClose {
            lazyListenersSubject.removeListener(argument, listener)
        }
    }
}