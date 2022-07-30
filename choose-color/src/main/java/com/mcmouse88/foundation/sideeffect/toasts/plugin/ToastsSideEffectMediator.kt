package com.mcmouse88.foundation.sideeffect.toasts.plugin

import android.content.Context
import android.widget.Toast
import com.mcmouse88.foundation.model.tasks.dispatcher.MainThreadDispatcher
import com.mcmouse88.foundation.sideeffect.SideEffectMediator
import com.mcmouse88.foundation.sideeffect.toasts.Toasts

class ToastsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Toasts {

    private val dispatcher = MainThreadDispatcher()

    override fun showToast(message: String) {
        dispatcher.dispatch {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}