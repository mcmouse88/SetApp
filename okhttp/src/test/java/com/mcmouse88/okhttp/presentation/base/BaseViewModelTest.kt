package com.mcmouse88.okhttp.presentation.base

import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.arranged
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.Test

class BaseViewModelTest : ViewModelTest() {

    /**
     * [InjectMockKs] создает объект за нас, а необходимые зависмости для создания объекта, уже
     * имеются в абстрактном классе [ViewModelTest], от которого и наследуется тестируемый класс.
     */
    @InjectMockKs
    lateinit var viewModel: BaseViewModel

    @Test
    fun logoutCallsLogout() {
        arranged()

        viewModel.logout()

        verify(exactly = 1) {
            accountsRepository.logout()
        }
    }

    @Test
    fun logErrorLogsError() {
        val exception = IllegalStateException()

        viewModel.logError(exception)

        verify(exactly = 1) {
            logger.error(any(), refEq(exception))
        }
    }
}