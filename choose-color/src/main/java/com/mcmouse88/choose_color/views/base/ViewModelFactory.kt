@file:Suppress("UNCHECKED_CAST")

package com.mcmouse88.choose_color.views.base

import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import com.mcmouse88.choose_color.ARG_SCREEN
import com.mcmouse88.choose_color.App
import com.mcmouse88.choose_color.MainViewModel
import java.lang.reflect.Constructor

inline fun <reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val application = requireActivity().application as App
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen

    val provider = ViewModelProvider(requireActivity(), AndroidViewModelFactory(application))
    val mainViewModel = provider[MainViewModel::class.java]

    val dependencies = listOf(screen, mainViewModel) + application.models
    ViewModelFactory(dependencies, this)
}

class ViewModelFactory(
    private val dependencies: List<Any>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {


    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructors = modelClass.constructors
        val constructor = constructors.maxByOrNull { it.typeParameters.size }
            ?: throw RuntimeException("ViewModel constructor is null")

        val dependenciesWithSavedState = dependencies + handle
        val arguments = findDependencies(constructor, dependenciesWithSavedState)
        return constructor.newInstance(*arguments.toTypedArray()) as T
    }

    private fun findDependencies(constructor: Constructor<*>, dependencies: List<Any>): List<Any> {
        val args = mutableListOf<Any>()
        constructor.parameterTypes.forEach { parameterClass ->
            val dependency = dependencies.first { parameterClass.isAssignableFrom(it.javaClass) }
            args.add(dependency)
        }
        return args
    }
}