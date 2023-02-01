package com.mcmouse88.catadapterespresso.robolectric.apps.di

import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class FakeRepositoriesModule {

    @[Provides Singleton]
    fun provideCatsRepository(): CatsRepository {
        return mockk()
    }
}