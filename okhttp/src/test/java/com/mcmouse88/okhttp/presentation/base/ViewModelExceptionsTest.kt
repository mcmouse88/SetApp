package com.mcmouse88.okhttp.presentation.base

import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.domain.AuthException
import com.mcmouse88.okhttp.domain.BackendException
import com.mcmouse88.okhttp.domain.ConnectionException
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.utils.requireValue
import org.junit.Assert
import org.junit.Test

abstract class ViewModelExceptionsTest<VM : BaseViewModel> : ViewModelTest() {

    abstract val viewModel: VM

    abstract fun arrangeWithException(e: Exception)

    abstract fun act()

    @Test
    fun safeLaunchWithConnectionExceptionShowsMessage() {
        val exception = ConnectionException(Exception())
        arrangeWithException(exception)
        act()
        Assert.assertEquals(
            R.string.connection_error,
            viewModel.showErrorMessageEvent.requireValue().get()
        )
    }

    @Test
    fun safeLaunchWithBackendExceptionShowsMessage() {
        val exception = BackendException(404, "some error message")
        arrangeWithException(exception)
        act()
        Assert.assertEquals(
            exception.message,
            viewModel.showErrorMessageEvent.requireValue().get()
        )
    }

    @Test
    fun safeLaunchWithAuthExceptionRestartsFromLoginScreen() {
        val exception = AuthException(Exception())
        arrangeWithException(exception)
        act()
        Assert.assertNotNull(viewModel.showAuthErrorAndRestartEvent.requireValue().get())
    }

    @Test
    fun safeLaunchWithOtherExceptionsShowsInternalErrorMessage() {
        val exception = IllegalStateException()
        arrangeWithException(exception)
        act()
        Assert.assertEquals(
            R.string.internal_error,
            viewModel.showErrorMessageResEvent.requireValue().get()
        )
    }
}