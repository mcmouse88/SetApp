package com.mcmouse88.okhttp.di

import com.mcmouse88.okhttp.utils.async.DefaultLazyFlowFactory
import com.mcmouse88.okhttp.utils.async.DefaultLazyListenersFactory
import com.mcmouse88.okhttp.utils.async.LazyFlowFactory
import com.mcmouse88.okhttp.utils.async.LazyListenersFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LazyFlowFactoryModule {

    @Binds
    fun bindLazyFlowFactory(
        factory: DefaultLazyFlowFactory
    ): LazyFlowFactory

    @Binds
    fun bindLazyListenerFactory(
        factory: DefaultLazyListenersFactory
    ): LazyListenersFactory
}