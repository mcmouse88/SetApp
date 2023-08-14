package com.mcmouse88.file_to_server.domain

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.mcmouse88.file_to_server.R
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
            Log.e(javaClass.simpleName, "Error!", exception)
            if (exception is AuthException) return
            Toast.makeText(context, getErrorMessage(exception), Toast.LENGTH_SHORT).show()
        }

        override fun getErrorMessage(exception: Throwable): String {
            return when (exception) {
                is CalledNotFromUiException -> context.getString(R.string.error_called_not_from_ui)
                is LoginFailedException -> context.getString(R.string.error_login_failed, exception.message)
                is AlreadyInProgressException -> context.getString(R.string.error_in_progress)
                is ConnectionException -> context.getString(R.string.error_connection)
                else -> context.getString(R.string.error_unknown)
            }
        }
    }
}

fun ErrorHandler.launchIn(
    scope: CoroutineScope,
    customErrorHandler: (Throwable) -> Boolean = { false },
    finally: () -> Unit = { /* no-op */ },
    block: suspend () -> Unit,
) {
    scope.launch {
        try {
            block()
        } catch (e: Exception) {
            if (e !is OperationCancelledException && customErrorHandler(e).not()) {
                handleError(e)
            }
        } finally {
            finally()
        }
    }
}

suspend fun suppressExceptions(block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Throwable) { /* no-op */ }
}