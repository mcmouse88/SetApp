package com.mcmouse88.foundation.views

import androidx.fragment.app.Fragment
import com.mcmouse88.choose_color.MainActivity

abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    fun notifyScreenUpdates() {
        (requireActivity() as FragmentsHolder).notifyScreenUpdate()
    }
}