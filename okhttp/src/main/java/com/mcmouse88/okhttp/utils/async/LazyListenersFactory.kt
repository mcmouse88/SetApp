package com.mcmouse88.okhttp.utils.async

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

interface LazyListenersFactory {

    fun<A : Any, T : Any> createLazyListenersSubject(
        loaderExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
        handlerExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
        loader: ValueLoader<A, T>
    ): LazyListenersSubject<A, T>
}