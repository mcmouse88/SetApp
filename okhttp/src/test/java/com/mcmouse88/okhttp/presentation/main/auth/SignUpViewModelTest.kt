package com.mcmouse88.okhttp.presentation.main.auth

import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.domain.AccountAlreadyExistException
import com.mcmouse88.okhttp.domain.EmptyFieldException
import com.mcmouse88.okhttp.domain.Field
import com.mcmouse88.okhttp.domain.PasswordMismatchException
import com.mcmouse88.okhttp.domain.accounts.entities.SignUpData
import com.mcmouse88.okhttp.presentation.base.ViewModelExceptionsTest
import com.mcmouse88.okhttp.presentation.main.auth.SignUpViewModel.Companion.NO_ERROR_MESSAGE
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

class SignUpViewModelTest : ViewModelTest() {

    @InjectMockKs
    lateinit var viewModel: SignUpViewModel

    @Test
    fun testInitialState() {
        val expectedState = createInitialState()

        val state = viewModel.state.requireValue()
        Assert.assertEquals(expectedState, state)
    }

    @Test
    fun signUpShowsProgress() {
        val expectedState = createInitialState(
            signUpInProgress = true
        )

        coEvery { accountsRepository.signUp(any()) } returnsSubject CoroutineSubject()
        viewModel.signUp(createSignUpData())
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpSendsDataToRepository() {
        val expectedData = createSignUpData()
        coEvery { accountsRepository.signUp(any()) } returnsSubject CoroutineSubject()
        viewModel.signUp(expectedData)
        coVerify(exactly = 1) { accountsRepository.signUp(expectedData) }
    }

    @Test
    fun signUpWithSuccessHidesProgress() {
        coEvery { accountsRepository.signUp(any()) } just runs
        viewModel.signUp(createSignUpData())
        Assert.assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signUpWithExceptionHidesProgress() {
        coEvery { accountsRepository.signUp(any()) } throws IllegalStateException("Oops")
        viewModel.signUp(createSignUpData())
        Assert.assertFalse(viewModel.state.requireValue().showProgress)
    }

    @Test
    fun signUpWithSuccessShowsMessageAndGoesBack() {
        coEvery { accountsRepository.signUp(any()) } just runs
        viewModel.signUp(createSignUpData())

        Assert.assertEquals(
            R.string.sign_up_success,
            viewModel.showToastEvent.requireValue().get()
        )

        Assert.assertNotNull(viewModel.goBackEvent.requireValue().get())
    }

    @Test
    fun signUpWithEmptyEmailExceptionShowsError() {
        val expectedState = createInitialState(
            emailErrorMessageRes = R.string.field_is_empty
        )
        coEvery { accountsRepository.signUp(any()) } throws EmptyFieldException(Field.Email)
        viewModel.signUp(createSignUpData())
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithEmptyUsernameExceptionShowsError() {
        val expectedState = createInitialState(
            userNameErrorMessageRes = R.string.field_is_empty
        )
        coEvery { accountsRepository.signUp(any()) } throws EmptyFieldException(Field.Username)
        viewModel.signUp(createSignUpData())
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithEmptyPasswordExceptionShowsError() {
        val expectedState = createInitialState(
            passwordErrorMessageRes = R.string.field_is_empty
        )
        coEvery { accountsRepository.signUp(any()) } throws EmptyFieldException(Field.Password)
        viewModel.signUp(createSignUpData())
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithPasswordMismatchExceptionShowsError() {
        val expectedState = createInitialState(
            repeatPasswordErrorMessageRes = R.string.password_mismatch
        )

        coEvery { accountsRepository.signUp(any()) } throws PasswordMismatchException()
        viewModel.signUp(createSignUpData())
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun signUpWithAccountAlreadyExistsExceptionsShowsError() {
        val expectedState = createInitialState(
            emailErrorMessageRes = R.string.account_already_exists
        )

        coEvery { accountsRepository.signUp(any()) } throws AccountAlreadyExistException(Exception())
        viewModel.signUp(createSignUpData())
        Assert.assertEquals(expectedState, viewModel.state.requireValue())
    }

    @Test
    fun stateShowProgressWithPendingOperationReturnsTrue() {
        val state = createInitialState(signUpInProgress = true)
        val showProgress = state.showProgress
        Assert.assertTrue(showProgress)
    }

    @Test
    fun stateEnableViewsWithPendingOperationReturnsFalse() {
        val state = createInitialState(signUpInProgress = true)
        val enableViews = state.enableViews
        Assert.assertFalse(enableViews)
    }

    @Test
    fun stateShowProgressWithoutPendingOperationReturnsFalse() {
        val state = createInitialState(signUpInProgress = false)
        val showProgress = state.showProgress
        Assert.assertFalse(showProgress)
    }

    @Test
    fun stateEnableViewsWithoutPendingOperationReturnsTrue() {
        val state = createInitialState(signUpInProgress = false)
        val enableViews = state.enableViews
        Assert.assertTrue(enableViews)
    }

    private fun createInitialState(
        emailErrorMessageRes: Int = NO_ERROR_MESSAGE,
        passwordErrorMessageRes: Int = NO_ERROR_MESSAGE,
        repeatPasswordErrorMessageRes: Int = NO_ERROR_MESSAGE,
        userNameErrorMessageRes: Int = NO_ERROR_MESSAGE,
        signUpInProgress: Boolean = false
    ) = SignUpViewModel.State(
        emailErrorMessageRes = emailErrorMessageRes,
        passwordErrorMessageRes = passwordErrorMessageRes,
        repeatPasswordErrorMessageRes = repeatPasswordErrorMessageRes,
        userNameErrorMessageRes = userNameErrorMessageRes,
        sighUpInProgress = signUpInProgress
    )

    private companion object {
        fun createSignUpData(
            username: String = "username",
            email: String = "email",
            password: String = "password",
            repeatPassword: String = "password"
        ) = SignUpData(
            username = username,
            email = email,
            password = password,
            repeatPassword = repeatPassword
        )
    }

    class SignUpExceptionsTest : ViewModelExceptionsTest<SignUpViewModel>() {

        @InjectMockKs
        override lateinit var viewModel: SignUpViewModel

        override fun arrangeWithException(e: Exception) {
            coEvery { accountsRepository.signUp(any()) } throws e
        }

        override fun act() {
            viewModel.signUp(createSignUpData())
        }
    }
}