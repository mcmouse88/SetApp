package com.mcmouse88.choose_color.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mcmouse88.choose_color.R
import com.mcmouse88.choose_color.databinding.PartResultBinding
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.views.BaseFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun<T> BaseFragment.renderSimpleResult(root: ViewGroup, result: Result<T>, onSuccess: (T) -> Unit) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onPending = {
            binding.resultProgressBar.visibility = View.VISIBLE
        },
        onError = {
            binding.errorContainer.visibility = View.VISIBLE
        },
        onSuccess = { data ->
            root.children
                .filter { it.id != R.id.result_progress_bar && it.id != R.id.error_container }
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(data)
        }
    )
}

fun BaseFragment.onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.button_try_again).setOnClickListener { onTryAgainPressed() }
}

/**
 * Чтобы работать с Flow на стороне фрагмента, нам нужно у интерфейса [LifecycleOwner]
 * (который как мы помним умирает после вызова метода onDestroy) получить значение
 * lifecycleScope и запустить корутину. Внутри корутины вызвать метод [repeatOnLifecycle()].
 * Участок кода объявленный внутри метода будет выполняться с того метода жизненного цикла,
 * который укажем в параметрах и будет приостановлен в противоположном методе (пример
 * onStart -> onStop, onCreate -> onDestroy)
 */
fun<T> BaseFragment.collectFlow(flow: Flow<T>, onCollect: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                onCollect(it)
            }
        }
    }
}