package com.mcmouse88.okhttp.source_retrofit.base

import com.mcmouse88.okhttp.app.model.SourcesProvider
import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.boxes.BoxesSource
import com.mcmouse88.okhttp.source_retrofit.accounts.RetrofitAccountsSource
import com.mcmouse88.okhttp.source_retrofit.boxes.RetrofitBoxesSource

class RetrofitSourcesProvider(
    private val config: RetrofitConfig
) : SourcesProvider {

    override fun getAccountsSource(): AccountsSource {
        return RetrofitAccountsSource(config)
    }

    override fun getBoxesSource(): BoxesSource {
        return RetrofitBoxesSource(config)
    }
}