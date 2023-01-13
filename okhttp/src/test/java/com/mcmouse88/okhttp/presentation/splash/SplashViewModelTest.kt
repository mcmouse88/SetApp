package com.mcmouse88.okhttp.presentation.splash

import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class SplashViewModelTest : ViewModelTest() {

    @Test
    fun splashViewModelWithSignedInUserSendsLaunchMainScreenWithTrueValue() {
        val accountsRepository = mockk<AccountsRepository>()
        every { accountsRepository.isSignedIn() } returns true
        val viewModel = SplashViewModel(accountsRepository)
        val isSignedIn = viewModel.launchMainScreenEvent.requireValue().get()!!
        Assert.assertTrue(isSignedIn)
    }

    @Test
    fun splashViewModelWithoutSignedInUserSendsLaunchMainScreenWithFalseValue() {
        val accountsRepository = mockk<AccountsRepository>()
        every { accountsRepository.isSignedIn() } returns false
        val viewModel = SplashViewModel(accountsRepository)
        val isSignedIn = viewModel.launchMainScreenEvent.requireValue().get()!!
        Assert.assertFalse(isSignedIn)
    }
}