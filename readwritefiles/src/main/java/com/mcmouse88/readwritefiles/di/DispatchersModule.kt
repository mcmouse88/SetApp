package com.mcmouse88.readwritefiles.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@[Qualifier Retention(AnnotationRetention.BINARY)]
annotation class IoDispatcher

@[Qualifier Retention(AnnotationRetention.BINARY)]
annotation class MainDispatcher

@[Module InstallIn(SingletonComponent::class)]
class DispatchersModule {

    @[Provides IoDispatcher]
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @[Provides MainDispatcher]
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}