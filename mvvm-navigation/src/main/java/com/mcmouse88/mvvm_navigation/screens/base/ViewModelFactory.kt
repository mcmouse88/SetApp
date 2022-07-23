package com.mcmouse88.mvvm_navigation.screens.base

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.mcmouse88.mvvm_navigation.MainActivity
import com.mcmouse88.mvvm_navigation.navigator.ARG_SCREEN
import com.mcmouse88.mvvm_navigation.navigator.MainNavigator
import com.mcmouse88.mvvm_navigation.navigator.Navigator

class ViewModelFactory(
    private val screen: BaseScreen,
    private val fragment: BaseFragment
) : ViewModelProvider.Factory {

    /**
     * В методе [onCreate()] нам понадобится Активити, которую получим из фрагмента,
     * application, который получим уже из активити, а также объект класса [ViewModelProvider],
     * с помощью которого можно получить доступ к навигатору из [ViewModelFactory], в конструктор
     * которого передаем объект класса [ViewModelStoreOwner], которым является Актитвити, и так
     * как нам нужна именно [AndroidViewModel], то передаем в конструктор фабрику по ее
     * созданию. Далее получаем конструктор
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val hostActivity = fragment.requireActivity()
        val application = hostActivity.application
        val provider = ViewModelProvider(hostActivity, AndroidViewModelFactory(application))
        val navigator = provider[MainNavigator::class.java]

        val constructor = modelClass.getConstructor(Navigator::class.java, screen::class.java)
        return constructor.newInstance(navigator, screen)
    }
}

/**
 * объект класса [Screen] мы можем получить из аргументов, так как мы передаем его каждый раз при
 * переходе во фрагментах
 */
inline fun<reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen
    ViewModelFactory(screen, this)
}