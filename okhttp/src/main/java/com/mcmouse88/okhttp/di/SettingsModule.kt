package com.mcmouse88.okhttp.di

import com.mcmouse88.okhttp.app.model.settings.AppSettings
import com.mcmouse88.okhttp.app.model.settings.SharedPreferencesAppSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * В отличии от даггера у hilt уже имеются собственные компоненты, которые выполняют большинство
 * необходимых задач по внедрению зависимостей, аннотация [InstallIn] это чисто hilt аннотация,
 * с помощью которой модуль добавляется в определенный компонент. [SingletonComponent] это
 * уже написанный для нас компонент, который используется для глобальных зависимостей. Например
 * аннотация [ViewModelComponent] отвечает за предоставление зависимостей в рамках конкретной
 * [ViewModel]. Аннотация [Singleton] используется для указания скоупа для сущности, если не
 * указывать скоуп, то hilt каждый раз будет создавать новый экземпляр интерфейса или класса.
 * Аннотацию для скоупа можно объявлять над функцией создающей сущность, так и над самим классом,
 * реализующим необходимый нам интерфейс. Если же нам нужен другой скоуп, например скоуп
 * соответствующий жизненному циклу активити, то тогда для модуля нужно указать не
 * [SingletonComponent], а [ActivityComponent], и над функцией или классов указать аннотацию
 * [ActivityScoped]
 */
@Module
@InstallIn(SingletonComponent::class)
// @InstallIn(ActivityComponent::class)
interface SettingsModule {

    @Binds
    @Singleton
    // @ActivityScoped
    fun bindAppSettings(
        appSettings: SharedPreferencesAppSettings
    ): AppSettings
}