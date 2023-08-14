package com.mcmouse88.file_to_server.di

import com.mcmouse88.file_to_server.domain.ErrorHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ErrorHandlerModule {

    @Binds
    fun bindsDefaultErrorToStringMapper(
        errorHandler: ErrorHandler.DefaultErrorHandler
    ): ErrorHandler
}