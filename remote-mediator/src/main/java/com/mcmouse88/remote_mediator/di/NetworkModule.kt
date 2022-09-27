package com.mcmouse88.remote_mediator.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mcmouse88.remote_mediator.data.retrofit.LaunchesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class NetworkModule {

    @[Provides Singleton]
    fun provideLaunchesApi(retrofit: Retrofit): LaunchesApi {
        return retrofit.create(LaunchesApi::class.java)
    }

    @[Provides Singleton]
    fun provideRetrofit(
        client: OkHttpClient,
        converter: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(converter)
            .client(client)
            .build()
    }

    @[Provides Singleton]
    fun provideOkHttpClient(@Named("loginInterceptor") interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @[Provides Singleton Named("loginInterceptor")]
    fun provideLoginInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @[Provides Singleton]
    fun provideConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @[Provides Singleton]
    fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    private companion object {
        const val BASE_URL = "https://api.spacexdata.com/v5/"
    }
}