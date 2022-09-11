package com.mcmouse88.okhttp.app.utiils

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.mcmouse88.okhttp.app.model.ResultResponse
import com.mcmouse88.okhttp.app.model.Success
import com.mcmouse88.okhttp.app.screens.base.BaseFragment
import com.mcmouse88.okhttp.app.views.ResultView

fun<T> LiveData<T>.requireValue(): T {
    return this.value ?: throw IllegalStateException("Value ${this.javaClass} is empty")
}

fun<T> LiveData<ResultResponse<T>>.observeResult(
    fragment: BaseFragment,
    root: View,
    resultView: ResultView,
    onSuccess: (T) -> Unit
) {
    observe(fragment.viewLifecycleOwner) { result ->
        resultView.setResult(fragment, result)
        val rootView: View = if (root is ScrollView) root.getChildAt(0) else root

        if (rootView is ViewGroup && rootView !is RecyclerView && root !is AbsListView) {
            rootView.children
                .filter { it != resultView }
                .forEach {
                    it.isVisible = result is Success<*>
                }
        }
        if (result is Success) onSuccess.invoke(result.value)
    }
}