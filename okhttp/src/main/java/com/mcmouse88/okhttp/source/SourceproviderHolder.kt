package com.mcmouse88.okhttp.source

import com.google.gson.Gson
import com.mcmouse88.okhttp.app.Const
import com.mcmouse88.okhttp.app.Singletons
import com.mcmouse88.okhttp.app.model.SourcesProvider
import com.mcmouse88.okhttp.app.model.settings.AppSettings
import com.mcmouse88.okhttp.source.base.OkHttpConfig
import com.mcmouse88.okhttp.source.base.OkHttpSourceProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object SourceProviderHolder {

    val sourcesProvider: SourcesProvider by lazy<SourcesProvider> {
        val config = OkHttpConfig(
            baseUrl = Const.BASE_URL,
            client = createOkHttpClient(),
            gson = Gson()
        )
        OkHttpSourceProvider(config)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createAuthorizationInterceptor(Singletons.appSettings))
            .addInterceptor(createLoginInterceptor())
            .build()
    }

    private fun createAuthorizationInterceptor(settings: AppSettings): Interceptor {
        return Interceptor { chain ->
            val newBuilder = chain.request().newBuilder()
            val token = settings.getCurrentToken()
            if (token != null) {
                newBuilder.addHeader("Authorization", token)
            }
            return@Interceptor chain.proceed(newBuilder.build())
        }
    }

    private fun createLoginInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}