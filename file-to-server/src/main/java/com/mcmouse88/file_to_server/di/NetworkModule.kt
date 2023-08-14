package com.mcmouse88.file_to_server.di

import android.content.Context
import com.google.gson.Gson
import com.mcmouse88.file_to_server.data.accounts.google.GoogleAuthInterceptor
import com.mcmouse88.file_to_server.data.exceptions.DefaultSourceExceptionMapper
import com.mcmouse88.file_to_server.data.exceptions.SourceExceptionMapper
import com.mcmouse88.file_to_server.data.files.google.GoogleDriveApi
import com.mcmouse88.file_to_server.data.files.google.GoogleDriveUploadApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class NetworkModule {

    @[Provides Singleton]
    fun provideGson(): Gson = Gson()

    @[Provides Singleton]
    fun provideGoogleDriveApi(
        @ApplicationContext context: Context
    ): GoogleDriveApi {
        return createRetrofit(BASE_URL, createClient(context))
            .create(GoogleDriveApi::class.java)
    }

    @[Provides Singleton]
    fun provideGoggleDriveUploadApi(
        @ApplicationContext context: Context
    ): GoogleDriveUploadApi {
        return createRetrofit(BASE_UPLOAD_URL, createUploadClient(context))
            .create(GoogleDriveUploadApi::class.java)
    }

    @[Provides Singleton]
    fun provideExceptionMapper(): SourceExceptionMapper {
        return DefaultSourceExceptionMapper()
    }

    private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createClient(context: Context): OkHttpClient {
        return buildClient(context, HttpLoggingInterceptor.Level.BODY)
    }

    private fun createUploadClient(context: Context): OkHttpClient {
        return buildClient(context, HttpLoggingInterceptor.Level.HEADERS)
    }

    private fun buildClient(context: Context, logLevel: HttpLoggingInterceptor.Level): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(GoogleAuthInterceptor(context))
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
            .build()
    }


    private companion object {
        private const val BASE_URL = "https://www.googleapis.com/drive/v3/"
        private const val BASE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v3/"
    }
}