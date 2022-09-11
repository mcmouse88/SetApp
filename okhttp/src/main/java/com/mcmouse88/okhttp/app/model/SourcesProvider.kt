package com.mcmouse88.okhttp.app.model

import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.boxes.BoxesSource

interface SourcesProvider {

    fun getAccountsSource(): AccountsSource

    fun getBoxesSource(): BoxesSource
}