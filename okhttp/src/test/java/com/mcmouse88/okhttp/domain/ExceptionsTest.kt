package com.mcmouse88.okhttp.domain

import com.mcmouse88.okhttp.test_utils.catch
import com.mcmouse88.okhttp.test_utils.wellDone
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExceptionsTest {

    lateinit var block: suspend () -> String

    @Before
    fun setUp() {
        block = mockk()
    }

    /**
     * В данных тестовых методах проверяется работа метода [wrapBackendExceptions], который работает
     * по следующему принципу, если в блоке кода происходит [BackendException] с кодом ошибки
     * 401, то метод обрабатывает это исключение и вместо него бросает [AuthException], если же
     * происходит [BackendException] с другим кодом ошибкки, то оно просто пробрасывается дальше,
     * исключения других видов никак не обрабатываются.
     */
    @Test
    fun wrapBackendExceptionsRethrowsOtherException() = runTest {
        val expectedException = IllegalStateException()
        coEvery { block() } throws expectedException

        val exception: IllegalStateException = catch {
            wrapBackendException(block)
        }

        Assert.assertSame(expectedException, exception)
    }

    @Test
    fun wrapBackendExceptionsMaps401ErrorToAuthException() = runTest {
        coEvery { block() } throws BackendException(
            code = 401,
            message = "Oops"
        )

        catch<AuthException> { wrapBackendException(block) }

        wellDone()
    }

    @Test
    fun wrapBackendExceptionDoesNotMapOtherBackendExceptions() = runTest {
        val expectedBackendException = BackendException(
            code = 432,
            message = "Boom!"
        )

        coEvery { block() } throws expectedBackendException

        val exception: BackendException = catch {
            wrapBackendException(block)
        }

        Assert.assertSame(expectedBackendException, exception)
    }
}