package com.mcmouse88.okhttp.presentation.main.auth

import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.domain.EmptyFieldException
import com.mcmouse88.okhttp.domain.Field
import com.mcmouse88.okhttp.domain.InvalidCredentialsException
import com.mcmouse88.okhttp.presentation.base.ViewModelExceptionsTest
import com.mcmouse88.okhttp.test_utils.CoroutineSubject
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.returnsSubject
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.just
import io.mockk.runs
import org.junit.Assert
import org.junit.Test

class SignInViewModelTest : ViewModelTest() {

    @InjectMockKs
    lateinit var viewModel: SignInViewModel

    @Test
    fun testInitialState() {
        val expectedState = SignInViewModel.State(
            emptyEmailError = false,
            emptyPasswordError = false,
            signInInProgress = false
        )

        val state = viewModel.state.requireValue()
        Assert.assertEquals(expectedState, state)
    }

    @Test
    fun signInShowsProgress() {
        coEvery { accountsRepository.signIn(any(), any()) } returnsSubject CoroutineSubject()

        viewModel.signIn("email", "password")
        Assert.assertTrue(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signInSendsCredentialsToRepository() {
        coEvery { accountsRepository.signIn(any(), any()) } returnsSubject CoroutineSubject()
        viewModel.signIn("email", "password")
        coVerify(exactly = 1) { accountsRepository.signIn("email", "password") }
    }

    @Test
    fun signInWithSuccessHidesProgress() {
        val subject = CoroutineSubject<Unit>()
        coEvery { accountsRepository.signIn(any(), any()) } returnsSubject subject
        viewModel.signIn("email", "password")
        Assert.assertTrue(viewModel.state.requireValue().showProgress)
        subject.sendSuccess(Unit)
        Assert.assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signInWithExceptionHidesProgress() {
        coEvery { accountsRepository.signIn(any(), any()) } throws IllegalStateException("Oops")
        viewModel.signIn("email", "password")
        Assert.assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signInWithEmptyEmailExceptionShowsError() {
        val expectedState = SignInViewModel.State(
            emptyPasswordError = false,
            emptyEmailError = true,
            signInInProgress = false
        )

        coEvery { accountsRepository.signIn(any(), any()) } throws EmptyFieldException(Field.Email)
        viewModel.signIn("email", "password")
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signInWithEmptyPasswordExceptionShowsError() {
        val expectedState = SignInViewModel.State(
            emptyPasswordError = true,
            emptyEmailError = false,
            signInInProgress = false
        )

        coEvery {
            accountsRepository.signIn(any(), any())
        } throws EmptyFieldException(Field.Password)

        viewModel.signIn("email", "password")
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signInWithInvalidCredentialsExceptionShowsErrorAndClearsPasswordField() {
        val expectedState = SignInViewModel.State(
            emptyPasswordError = false,
            emptyEmailError = false,
            signInInProgress = true
        )

        coEvery {
            accountsRepository.signIn(any(), any())
        } throws InvalidCredentialsException(Exception())

        viewModel.signIn("email", "password")
        Assert.assertNotNull(viewModel.clearPasswordEvent.requireValue().get())
        Assert.assertEquals(
            R.string.invalid_email_or_password,
            viewModel.showAuthErrorToastEvent.requireValue().get()
        )

        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signInWithSuccessLaunchesTabsScreen() {
        coEvery { accountsRepository.signIn(any(), any()) } just runs
        viewModel.signIn("email", "password")
        Assert.assertNotNull(viewModel.navigateToTabsEvent.requireValue().get())
    }

    class SignInExceptionsTest : ViewModelExceptionsTest<SignInViewModel>() {

        @InjectMockKs
        override lateinit var viewModel: SignInViewModel

        override fun arrangeWithException(e: Exception) {
            coEvery { accountsRepository.signIn(any(), any()) } throws e
        }

        override fun act() {
            viewModel.signIn("email", "password")
        }
    }
}