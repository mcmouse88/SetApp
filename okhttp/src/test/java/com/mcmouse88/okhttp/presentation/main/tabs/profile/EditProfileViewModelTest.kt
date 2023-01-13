package com.mcmouse88.okhttp.presentation.main.tabs.profile

import com.mcmouse88.okhttp.R
import com.mcmouse88.okhttp.domain.*
import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.presentation.base.ViewModelExceptionsTest
import com.mcmouse88.okhttp.test_utils.*
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EditProfileViewModelTest : ViewModelTest() {

    private lateinit var flow: MutableStateFlow<ResultResponse<Account>>

    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setUp() {
        flow = MutableStateFlow(Pending())
        every { accountsRepository.getAccount() } returns flow
        viewModel = EditProfileViewModel(accountsRepository, logger)
    }

    @Test
    fun saveUsernameShowsProgress() {
        coEvery {
            accountsRepository.updateAccountUsername(any())
        } returnsSubject CoroutineSubject()

        viewModel.saveUsername("username")
        Assert.assertTrue(viewModel.saveInProgress.requireValue())
    }

    @Test
    fun saveUsernameSendUsernameToRepository() {
        coEvery {
            accountsRepository.updateAccountUsername(any())
        } returnsSubject CoroutineSubject()

        viewModel.saveUsername("username")
        coVerify(exactly = 1) {
            accountsRepository.updateAccountUsername("username")
        }
    }

    @Test
    fun saveUsernameWithSuccessHidesProgressAndGoesBack() {
        coEvery { accountsRepository.updateAccountUsername(any()) } just runs
        viewModel.saveUsername("username")
        Assert.assertFalse(viewModel.saveInProgress.requireValue())
        Assert.assertNotNull(viewModel.goBackEvent.requireValue().get())
    }

    @Test
    fun saveUserNameWithErrorHidesProgress() {
        coEvery { accountsRepository.updateAccountUsername(any()) } throws IllegalStateException()
        viewModel.saveUsername("username")
        Assert.assertFalse(viewModel.saveInProgress.requireValue())
        Assert.assertNull(viewModel.goBackEvent.value?.get())
    }

    @Test
    fun saveUsernameWithEmptyValueShowsError() {
        coEvery {
            accountsRepository.updateAccountUsername(any())
        } throws EmptyFieldException(Field.Username)

        viewModel.saveUsername("username")
        Assert.assertEquals(
            R.string.field_is_empty,
            viewModel.showErrorMessageResEvent.requireValue().get()
        )
    }

    @Test
    fun initialUsernameEventReturnsFirstValueFromRepository() {
        arranged()
        flow.value = Success(createAccount(username = "username1"))
        val value1 = viewModel.initialUserNameEvent.value?.get()
        flow.value = Success(createAccount(username = "username2"))
        val value2 = viewModel.initialUserNameEvent.value?.get()

        Assert.assertEquals("username1", value1)
        Assert.assertNull(value2)
    }

    class SaveUsernameExceptionsTest : ViewModelExceptionsTest<EditProfileViewModel>() {

        override lateinit var viewModel: EditProfileViewModel

        @Before
        fun setUp() {
            every { accountsRepository.getAccount() } returns flowOf(Success(createAccount()))
            viewModel = EditProfileViewModel(accountsRepository, logger)
        }

        override fun arrangeWithException(e: Exception) {
            coEvery { accountsRepository.updateAccountUsername(any()) } throws e
        }

        override fun act() {
            viewModel.saveUsername("username")
        }
    }
}