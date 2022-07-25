package com.mcmouse88.foundation.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.mcmouse88.choose_color.R
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.PendingResult
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.model.SuccessResult

abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    fun notifyScreenUpdates() {
        (requireActivity() as FragmentsHolder).notifyScreenUpdate()
    }

    /**
     * Так как обработку результата возможно придется делать в о многих фрагмента, вынесем эту
     * логику в базовый класс для всех фрагментов.
     * ```css
     * root.children.forEach { it.visibility = View.GONE }
     * ```
     * Данная строка перебирает все View внутри компонента.
     */
    fun<T> renderResult(
        root: ViewGroup,
        result: Result<T>,
        onPending: () -> Unit,
        onError: (Exception) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        root.children.forEach { it.visibility = View.GONE }
        when(result) {
            is SuccessResult -> onSuccess(result.data)
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
        }
    }
}

fun BaseFragment.onTryAgain(root: View, onTryAgain: () -> Unit) {
    root.findViewById<Button>(R.id.button_try_again).setOnClickListener {
        onTryAgain()
    }
}