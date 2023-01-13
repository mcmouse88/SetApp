package com.mcmouse88.okhttp.presentation.main

import com.mcmouse88.okhttp.domain.Empty
import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.createAccount
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Test

class MainActivityViewModelTest : ViewModelTest() {

    @Test
    fun mainViewModelSharesUsernameOfCurrentUser() {
        val account = createAccount(username = "username")
        every { accountsRepository.getAccount() } returns flowOf(Success(account))
        val viewModel = MainActivityViewModel(accountsRepository)
        val username = viewModel.username.value
        Assert.assertEquals("@username", username)
    }

    @Test
    fun mainViewModelSharesEmptyStringIfCurrentUserUnavailable() {
        every { accountsRepository.getAccount() } returns flowOf(Empty())
        val viewModel = MainActivityViewModel(accountsRepository)
        val username = viewModel.username.value
        Assert.assertEquals("", username)
    }

    @Test
    fun mainViewModelListensForFurtherUsernameUpdates() {
        val flow: MutableStateFlow<ResultResponse<Account>> = MutableStateFlow(Success(createAccount(username = "username1")))
        every { accountsRepository.getAccount() } returns flow
        val viewModel = MainActivityViewModel(accountsRepository)

        val username1 = viewModel.username.value
        flow.value = Pending()
        val username2 = viewModel.username.value
        flow.value = Success(createAccount(username = "username2"))
        val username3 = viewModel.username.value

        Assert.assertEquals("@username1", username1)
        Assert.assertEquals("", username2)
        Assert.assertEquals("@username2", username3)
    }
}