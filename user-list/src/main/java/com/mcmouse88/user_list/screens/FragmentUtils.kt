package com.mcmouse88.user_list.screens

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mcmouse88.user_list.viewmodel.UserListViewModel
import com.mcmouse88.user_list.App
import com.mcmouse88.user_list.Navigator
import com.mcmouse88.user_list.viewmodel.UserDetailViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass) {
            UserListViewModel::class.java -> {
                UserListViewModel(app.userService)
            }
            UserDetailViewModel::class.java -> {
                UserDetailViewModel(app.userService)
            }
            else -> throw IllegalArgumentException("Unknown modelClass: $modelClass")
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)

fun Fragment.navigator() = requireActivity() as Navigator