package com.mcmouse88.okhttp.presentation.main.tabs.profile

import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.arranged
import com.mcmouse88.okhttp.test_utils.createAccount
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProfileViewModelTest : ViewModelTest() {

    private lateinit var flow: MutableStateFlow<ResultResponse<Account>>
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        flow = MutableStateFlow(Pending())
        every { accountsRepository.getAccount() } returns flow
        viewModel = ProfileViewModel(accountsRepository, logger)
    }

    @Test
    fun reloadReloadsAccount() {
        arranged()
        viewModel.reload()
        verify(exactly = 1) { accountsRepository.reloadAccount() }
    }

    @Test
    fun accountsReturnsDataFromRepository() {
        val expectedAccount1 = Pending<Account>()
        val expectedAccount2 = createAccount(id = 2, username = "name2")
        val expectedAccount3 = createAccount(id = 3, username = "name3")

        flow.value = expectedAccount1
        val result1 = viewModel.account.requireValue()
        flow.value = Success(expectedAccount2)
        val result2 = viewModel.account.requireValue()
        flow.value = Success(expectedAccount3)
        val result3 = viewModel.account.requireValue()

        Assert.assertEquals(expectedAccount1, result1)
        Assert.assertEquals(expectedAccount2, result2.getValueOrNull())
        Assert.assertEquals(expectedAccount3, result3.getValueOrNull())
    }
}