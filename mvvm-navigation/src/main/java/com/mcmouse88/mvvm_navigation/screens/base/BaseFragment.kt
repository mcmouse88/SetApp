package com.mcmouse88.mvvm_navigation.screens.base

import androidx.fragment.app.Fragment

/**
 * От данного класса будем наследовать все фрагменты, при создании новых экранов. Также внутри
 * него мы уже определили абстрактное поле viewModel, в котором будет хранится ViewModel,
 * которая управляет этим фрагментом.
 */
abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel
}