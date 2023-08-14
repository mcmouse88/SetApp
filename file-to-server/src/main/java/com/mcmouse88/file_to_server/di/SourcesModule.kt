package com.mcmouse88.file_to_server.di

import com.mcmouse88.file_to_server.data.ActivityRequired
import com.mcmouse88.file_to_server.data.accounts.AccountsSource
import com.mcmouse88.file_to_server.data.accounts.google.GoogleAccountsSource
import com.mcmouse88.file_to_server.data.chooser.AndroidFileChooser
import com.mcmouse88.file_to_server.data.chooser.FileChooser
import com.mcmouse88.file_to_server.data.files.FilesSource
import com.mcmouse88.file_to_server.data.files.google.GoogleFilesSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@[Module InstallIn(SingletonComponent::class)]
class SourcesModule {

    @Provides
    fun bindsGoogleAccountsSourceAsSource(source: GoogleAccountsSource): AccountsSource {
        return source
    }

    @[Provides IntoSet]
    fun bindsGoogleAccountsSourceAsActivityRequired(source: GoogleAccountsSource): ActivityRequired {
        return source
    }

    @Provides
    fun bindsFilesSource(source: GoogleFilesSource): FilesSource {
        return source
    }

    @Provides
    fun bindsFileChooser(androidChooser: AndroidFileChooser): FileChooser {
        return androidChooser
    }

    @[Provides IntoSet]
    fun bindsAndroidFileChooserAsActivityRequired(chooser: AndroidFileChooser): ActivityRequired {
        return chooser
    }
}