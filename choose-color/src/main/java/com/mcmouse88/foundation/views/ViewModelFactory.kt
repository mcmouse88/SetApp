@file:Suppress("UNCHECKED_CAST")

package com.mcmouse88.foundation.views

import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.mcmouse88.foundation.ARG_SCREEN
import com.mcmouse88.foundation.ActivityScopeViewModel
import com.mcmouse88.foundation.BaseApplication
import java.lang.reflect.Constructor

/**
 * Список зависимостей формируется из нескольких scope. [application] содержит список моделей,
 * также присутствует [ActivityScopeViewModel], которая содержит в себе [Navigator] и [UiAction], а также
 * scope самого экрана в виде переменной [screen]
 */
inline fun <reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val application = requireActivity().application as BaseApplication
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen

    val activityScopeViewModel = (requireActivity() as FragmentsHolder).getActivityScopeViewModel()

    val dependencies = listOf(screen, activityScopeViewModel) + application.singletonScopeDependencies
    ViewModelFactory(dependencies, this)
}

/**
 * Так как у нас [ViewModel] поддерживает [SavedStateHandle], то фабрику мы наследуем не от
 * [ViewModelProvider.Factory] как обычно, а от [AbstractSavedStateViewModelFactory], и тогда
 * объект [SavedStateHandle] придет нам в качестве аргумента в метод [create()]. В конструктор
 * фабрики мы передаем список всех возможных зависимостей
 * ```css
 * private val dependencies: List<Any>
 * ```
 *
 */
class ViewModelFactory(
    private val dependencies: List<Any>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    /**
     * В методе [create()] мы получаем список всех конструкторов с параметрами, далее получаем
     * конструктор с максимальным количеством параметров. Далее формируем список зависимостей, на
     * основании тех зависимостей, которые нам передали в конструктор фабрики и добавлем в него
     * объект [SavedStateHandle], который пришел в качестве параметра в метод. В результате чего
     * у нас получился список со всеми возможными зависимостями для [ViewModel]. Далее получаем
     * аргументы, при помощи метода [findDependencies()], в который передаем конструктор с
     * максимальными параметрами и сформированный список зависимостей. этот метод сформирует нам
     * аргументы для конструктора конкретной [ViewModel], то есть например в конструкторе
     * конкретной [ViewModel] конструктор с 5-ю паарметрами, данный метод сформирует
     * нам список параметров в том же порядке, в котором они находятся в конструкторе. Таким
     * образом можно будет безопасно вызвать метод [newInstance()], и объект [ViewModel]
     * нормально создасться.
     */
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

    /**
     * Данный меетод берет конструктор, который приходит в качестве аргумента, проходит по всем
     * его параметрам, и ищет параметр соответствующего типа в списке зависимостей, и когда его
     * находит просто добавляет его в список, который потом возвращает.
     */
    private fun findDependencies(constructor: Constructor<*>, dependencies: List<Any>): List<Any> {
        val args = mutableListOf<Any>()
        constructor.parameterTypes.forEach { parameterClass ->
            val dependency = dependencies.first { parameterClass.isAssignableFrom(it.javaClass) }
            args.add(dependency)
        }
        return args
    }
}