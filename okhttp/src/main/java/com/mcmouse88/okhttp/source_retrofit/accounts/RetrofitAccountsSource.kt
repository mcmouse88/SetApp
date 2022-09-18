package com.mcmouse88.okhttp.source_retrofit.accounts

import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.accounts.entities.Account
import com.mcmouse88.okhttp.app.model.accounts.entities.SignUpData
import com.mcmouse88.okhttp.source_retrofit.accounts.entities.SignInRequestEntity
import com.mcmouse88.okhttp.source_retrofit.accounts.entities.SignUpRequestEntity
import com.mcmouse88.okhttp.source_retrofit.accounts.entities.UpdateUsernameRequestEntity
import com.mcmouse88.okhttp.source_retrofit.base.BaseRetrofitSource
import com.mcmouse88.okhttp.source_retrofit.base.RetrofitConfig
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitAccountsSource @Inject constructor(
    config: RetrofitConfig
) : BaseRetrofitSource(config), AccountsSource {

    private val accountsApi = retrofit.create(AccountsApi::class.java)

    override suspend fun signIn(email: String, password: String): String = wrapRetrofitException {
        delay(1_000)
        val signInRequestEntity = SignInRequestEntity(
            email = email,
            password = password
        )
        accountsApi.signIn(signInRequestEntity).token
    }

    override suspend fun signUp(signUpData: SignUpData) = wrapRetrofitException {
        delay(1_000)
        val signUpRequestEntity = SignUpRequestEntity(
            email = signUpData.email,
            username = signUpData.username,
            password = signUpData.password
        )
        accountsApi.signUp(signUpRequestEntity)
    }

    override suspend fun getAccount(): Account = wrapRetrofitException {
        delay(1_000)
        accountsApi.getAccount().toAccount()
    }

    override suspend fun setUsername(username: String) = wrapRetrofitException {
        delay(1_000)
        val updateUsernameRequestEntity = UpdateUsernameRequestEntity(
            username = username
        )
        accountsApi.setUserName(updateUsernameRequestEntity)
    }
}