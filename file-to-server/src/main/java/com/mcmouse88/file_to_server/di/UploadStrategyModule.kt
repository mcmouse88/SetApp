package com.mcmouse88.file_to_server.di

import com.mcmouse88.file_to_server.data.files.google.upload.MultipartUploadStrategy
import com.mcmouse88.file_to_server.data.files.google.upload.UploadStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface UploadStrategyModule {

    @Binds
    fun bindsUploadStrategy(
        multipartUploadStrategy: MultipartUploadStrategy
    ): UploadStrategy
}