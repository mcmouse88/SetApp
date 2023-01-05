package com.mcmouse88.okhttp.utils.async

interface LazyFlowFactory {

    fun<A : Any, T : Any> createLazyFlowSubject(
        loader: SuspendValueLoader<A, T>
    ): LazyFlowSubject<A, T>
}