package com.mcmouse88.foundation.views

import androidx.fragment.app.Fragment
import com.mcmouse88.choose_color.MainActivity

abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    /**
     * Так как у нас всего одна Активити, то данный каст будет всегда успешным, если бы их
     * было несколько, данный подход не сработал бы.
     */
    fun notifyScreenUpdates() {
        (requireActivity() as MainActivity).notifyScreenUpdates()
    }
}