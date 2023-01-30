package com.mcmouse88.multi_choice_list.di

import com.mcmouse88.multi_choice_list.domain.Cat
import com.mcmouse88.multi_choice_list.multi_choice.MultiChoiceHandler
import com.mcmouse88.multi_choice_list.multi_choice.SimpleCatMultiChoiceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Qualifier

@[Qualifier Retention(AnnotationRetention.BINARY)]
annotation class CatsMultiChoice

@[Module InstallIn(ViewModelComponent::class)]
class CatsMultiChoiceModule {

    @[Provides CatsMultiChoice]
    fun provideMultiChoiceHandler(): MultiChoiceHandler<Cat> {
        return SimpleCatMultiChoiceHandler()
    }
}