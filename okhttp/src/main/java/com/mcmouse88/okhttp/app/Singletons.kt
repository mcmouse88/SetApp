package com.mcmouse88.okhttp.app

import android.content.Context
import com.mcmouse88.okhttp.app.model.SourcesProvider
import com.mcmouse88.okhttp.app.model.accounts.AccountsRepository
import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.boxes.BoxesRepository
import com.mcmouse88.okhttp.app.model.boxes.BoxesSource
import com.mcmouse88.okhttp.app.model.settings.AppSettings
import com.mcmouse88.okhttp.app.model.settings.SharedPreferencesAppSettings
import com.mcmouse88.okhttp.source.SourceProviderHolder

object Singletons {

    private lateinit var appContext: Context

    private val sourcesProvider: SourcesProvider by lazy {
        SourceProviderHolder.sourcesProvider
    }

    val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    private val accountsSource: AccountsSource by lazy {
        sourcesProvider.getAccountsSource()
    }

    private val boxesSource: BoxesSource by lazy {
        sourcesProvider.getBoxesSource()
    }

    val accountsRepository: AccountsRepository by lazy {
        AccountsRepository(
            accountsSource = accountsSource,
            appSettings = appSettings
        )
    }

    val boxesRepository: BoxesRepository by lazy {
        BoxesRepository(
            accountsRepository = accountsRepository,
            boxesSource = boxesSource
        )
    }

    fun init(appContext: Context) {
        Singletons.appContext = appContext
    }
}