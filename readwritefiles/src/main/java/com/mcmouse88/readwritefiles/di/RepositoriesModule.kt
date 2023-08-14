package com.mcmouse88.readwritefiles.di

import com.mcmouse88.readwritefiles.ActivityRequired
import com.mcmouse88.readwritefiles.model.AndroidFilesRepository
import com.mcmouse88.readwritefiles.model.FilesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@[Module InstallIn(SingletonComponent::class)]
interface RepositoriesModule {

    @Binds
    fun bindFilesRepositoryAsRepository(
        androidFilesRepository: AndroidFilesRepository
    ): FilesRepository

    @[Binds IntoSet]
    fun bindFilesRepositoryAsActivityRequired(
        androidFilesRepository: AndroidFilesRepository
    ): ActivityRequired
}