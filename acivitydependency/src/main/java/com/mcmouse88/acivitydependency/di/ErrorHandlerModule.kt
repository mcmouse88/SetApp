package com.mcmouse88.acivitydependency.di

import com.mcmouse88.acivitydependency.domain.ErrorHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ErrorHandlerModule {

    @Binds
    fun bindDefaultErrorToStringMapper(
        errorHandler: ErrorHandler.DefaultErrorHandler
    ): ErrorHandler
}