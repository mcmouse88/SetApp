package com.mcmouse88.foundation.uiactions

import android.content.Context
import android.widget.Toast

class AndroidUiActions(
    private val appContext: Context
) : UiActions {

    override fun showToast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Символ * указывает компилятору на то, что мы ему передаем не массив элементов переданных
     * как varargs, а именно как vararg, то есть каждый элемент по отдельности
     */
    override fun getString(messageRes: Int, vararg args: Any): String {
        return appContext.getString(messageRes, *args)
    }
}