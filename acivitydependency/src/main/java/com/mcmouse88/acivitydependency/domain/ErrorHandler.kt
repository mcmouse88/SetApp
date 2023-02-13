package com.mcmouse88.acivitydependency.domain

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mcmouse88.acivitydependency.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ErrorHandler {

    fun handleError(exception: Throwable)

    fun getErrorMessage(exception: Throwable): String

    @Singleton
    class DefaultErrorHandler @Inject constructor(
        @ApplicationContext private val context: Context
    ) : ErrorHandler {
        override fun handleError(exception: Throwable) {
            Toast.makeText(context, getErrorMessage(exception), Toast.LENGTH_SHORT).show()
            Log.e(javaClass.simpleName, "Error!!!", exception)
        }

        override fun getErrorMessage(exception: Throwable): String {
            return when (exception) {
                is AuthException -> context.getString(R.string.error_auth)
                is CalledNotFromUiException -> context.getString(R.string.error_called_not_from_ui)
                is LoginCancelledException -> context.getString(R.string.error_login_cancelled)
                is LoginFailedException -> context.getString(
                    R.string.error_login_failed,
                    exception.message
                )
                is AlreadyInProgressException -> context.getString(R.string.error_in_progress)
                else -> context.getString(R.string.error_unknown)
            }
        }
    }
}

fun ErrorHandler.launchIn(
    scope: CoroutineScope,
    customErrorHandler: (Throwable) -> Boolean = { false },
    block: suspend () -> Unit
) {
    scope.launch {
        try {
            block()
        } catch (e: Exception) {
            if (customErrorHandler(e).not()) {
                handleError(e)
            }
        }
    }
}