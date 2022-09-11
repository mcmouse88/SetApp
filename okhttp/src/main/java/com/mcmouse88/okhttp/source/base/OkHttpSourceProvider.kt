package com.mcmouse88.okhttp.source.base

import com.mcmouse88.okhttp.app.model.SourcesProvider
import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.boxes.BoxesSource
import com.mcmouse88.okhttp.source.accounts.OkHttpAccountsSource
import com.mcmouse88.okhttp.source.boxes.OkHttpBoxesSource

class OkHttpSourceProvider(
    private val config: OkHttpConfig
) : SourcesProvider {

    override fun getAccountsSource(): AccountsSource {
        return OkHttpAccountsSource(config)
    }

    override fun getBoxesSource(): BoxesSource {
        return OkHttpBoxesSource(config)
    }
}