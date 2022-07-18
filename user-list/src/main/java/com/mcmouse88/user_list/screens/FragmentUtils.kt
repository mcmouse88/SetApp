package com.mcmouse88.user_list.screens

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mcmouse88.user_list.viewmodel.UserListViewModel
import com.mcmouse88.user_list.App
import com.mcmouse88.user_list.Navigator
import com.mcmouse88.user_list.viewmodel.UserDetailViewModel

typealias ViewModelCreator = (App) -> ViewModel?

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val app: App,
    private val viewModelCreator: ViewModelCreator = { null }
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass) {
            UserListViewModel::class.java -> {
                UserListViewModel(app.userService)
            }
            else -> {
                viewModelCreator(app) ?:
                throw IllegalArgumentException("Unknown modelClass: $modelClass")
            }
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)

fun Fragment.navigator() = requireActivity() as Navigator

/**
 * [inline] метод кроме того, что не создает внутри себя объект анонимного класса, он еще в месте его
 * вызова подставляет код, который написан внутри метода, без вызова самого метода. Ключевое
 * слово [reified] можно использовать только с [inline] методом. [reified] упрощает запись, так
 * как при создании [ViewModel] нам нужно передавать еще и сам класс [ViewModel] в качестве
 * параметра в конструктор метода, то с ключевым словом [reified] при указании типа [ViewModel] в
 * diamond операторе он будет передаваться в конструктор метода автоматически. [noinline]
 * используется для того, чтобы  - так как по умолчанию все лямбды, которые мы передаем в [inline]
 * метод удаляются, и разворачиваются в коде без лямбд, но так как внутри [inline] функции мы
 * используем еще одну лямбду в виде:
 * ```css
 * typealias ViewModelCreator = (App) -> ViewModel?
 * ```
 * то без указания ключевого слова [noinline] его лямбда тоже будет удалена компилятором и мы
 * получим ошибку.
 */
inline fun<reified VM : ViewModel> Fragment.viewModelCreator(noinline creator: ViewModelCreator): Lazy<VM> {
    return viewModels { ViewModelFactory(requireContext().applicationContext as App, creator) }
}