package com.mcmouse88.okhttp.source.accounts

import com.mcmouse88.okhttp.app.model.accounts.AccountsSource
import com.mcmouse88.okhttp.app.model.accounts.entities.Account
import com.mcmouse88.okhttp.app.model.accounts.entities.SignUpData
import com.mcmouse88.okhttp.source.accounts.entities.*
import com.mcmouse88.okhttp.source.base.BaseOkHttpSource
import com.mcmouse88.okhttp.source.base.OkHttpConfig
import kotlinx.coroutines.delay
import okhttp3.Request

class OkHttpAccountsSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config), AccountsSource {

    /**
     * Для добавления заголовков к запросу используется методы внутри билдера [addHeader] и
     * [header], отличаются они тем, что в первом можно добавить сразу несколько значений для
     * заголовка, а во втором нет.
     */
    override suspend fun signIn(email: String, password: String): String {
        delay(1_000)
        val signInRequestEntity = SignInRequestEntity(
            email = email,
            password = password
        )
        val request = Request.Builder()
            .post(signInRequestEntity.toJsonRequestBody())
            .endpoint("/sign-in")
            .build()

        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<SignInResponseEntity>().token
    }

    override suspend fun signUp(signUpData: SignUpData) {
        delay(1_000)
        val signUpRequestEntity = SignUpRequestEntity(
            email = signUpData.email,
            username = signUpData.username,
            password = signUpData.password
        )
        val request = Request.Builder()
            .post(signUpRequestEntity.toJsonRequestBody())
            .endpoint("/sign-up")
            .build()
        client.newCall(request).suspendEnqueue()
    }

    override suspend fun getAccount(): Account {
        delay(1_000)
        val request = Request.Builder()
            .get()
            .endpoint("/me")
            .build()
        val response = client.newCall(request).suspendEnqueue()
        return response.parseJsonResponse<GetAccountResponseEntity>().toAccount()
    }

    override suspend fun setUsername(username: String) {
        delay(1_000)
        val updateUserNameRequestEntity = UpdateUsernameRequestEntity(
            username = username
        )
        val request = Request.Builder()
            .put(updateUserNameRequestEntity.toJsonRequestBody())
            .endpoint("/me")
            .build()
        client.newCall(request).suspendEnqueue()
    }
}