package com.mcmouse88.okhttp.di

import com.mcmouse88.okhttp.data.accounts.RetrofitAccountsSource
import com.mcmouse88.okhttp.data.boxes.RetrofitBoxesSource
import com.mcmouse88.okhttp.domain.accounts.AccountsSource
import com.mcmouse88.okhttp.domain.boxes.BoxesSource
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