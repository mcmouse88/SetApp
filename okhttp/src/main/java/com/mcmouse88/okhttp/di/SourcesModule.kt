package com.mcmouse88.okhttp.di

import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.boxes.BoxesSource
import com.mcmouse88.okhttp.source_retrofit.accounts.RetrofitAccountsSource
import com.mcmouse88.okhttp.source_retrofit.boxes.RetrofitBoxesSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SourcesModule {

    @Binds
    fun bindAccountsSource(
        retrofitAccountsSource: RetrofitAccountsSource
    ): AccountsSource

    @Binds
    fun bindBoxesSource(
        retrofitBoxesSource: RetrofitBoxesSource
    ): BoxesSource
}