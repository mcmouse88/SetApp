package com.mcmouse88.choose_color.views.base

import androidx.fragment.app.Fragment
import com.mcmouse88.choose_color.MainActivity

abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    fun notifyScreenUpdates() {
        (requireActivity() as MainActivity).notifyScreenUpdates()
    }
}