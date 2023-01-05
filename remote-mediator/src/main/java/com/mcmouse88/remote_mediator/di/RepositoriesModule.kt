package com.mcmouse88.remote_mediator.di

import com.mcmouse88.remote_mediator.data.DefaultLaunchesRepository
import com.mcmouse88.remote_mediator.domain.LaunchesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface RepositoriesModule {

    @Binds
    fun bindLaunchesRepository(
        launchesRepository: DefaultLaunchesRepository
    ): LaunchesRepository
}