package com.mcmouse88.remote_mediator.di

import android.content.Context
import androidx.room.Room
import com.mcmouse88.remote_mediator.data.room.AppDatabase
import com.mcmouse88.remote_mediator.data.room.LaunchesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class DatabaseModule {

    @[Singleton Provides]
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun provideLaunchesDao(database: AppDatabase): LaunchesDao {
        return database.getLaunchesDao()
    }

    private companion object {
        const val DB_NAME = "launches.db"
    }
}