package com.mcmouse88.okhttp.di

import com.mcmouse88.okhttp.utils.logger.LogcatLogger
import com.mcmouse88.okhttp.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *  Для того, чтобы заинжектить object class не нужно указывать аннотацию [Singleton] над методом.
 *  И так как у класса object нет конструктора, то аннотацию [Inject] также указывать не нужно.
 */
@Module
@InstallIn(SingletonComponent::class)
class LoggerModule {

    @Provides
    fun provideLogger(): Logger = LogcatLogger
}