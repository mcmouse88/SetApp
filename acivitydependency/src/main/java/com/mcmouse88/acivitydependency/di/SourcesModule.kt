package com.mcmouse88.acivitydependency.di

import com.mcmouse88.acivitydependency.data.ActivityRequired
import com.mcmouse88.acivitydependency.data.account.fake.FakeAccountsSource
import com.mcmouse88.acivitydependency.domain.accounts.AccountSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@[Module InstallIn(SingletonComponent::class)]
class SourcesModule {

    @Provides
    fun provideSourceAsSource(source: FakeAccountsSource): AccountSource {
        return source
    }

    /**
     * Аннотация [IntoSet] аннотация для мультибайндинга которая будет возвращать все классы
     * реализущие интерфейс [ActivityRequired]
     */
    @[Provides IntoSet]
    fun bindSourceAsActivityRequired(source: FakeAccountsSource): ActivityRequired {
        return source
    }
}