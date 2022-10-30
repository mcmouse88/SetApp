package com.mcmouse88.okhttp.data.accounts


import com.mcmouse88.okhttp.data.accounts.entities.GetAccountResponseEntity
import com.mcmouse88.okhttp.data.accounts.entities.SignInRequestEntity
import com.mcmouse88.okhttp.data.accounts.entities.SignInResponseEntity
import com.mcmouse88.okhttp.data.accounts.entities.SignUpRequestEntity
import com.mcmouse88.okhttp.data.accounts.entities.UpdateUsernameRequestEntity
import com.mcmouse88.okhttp.data.base.RetrofitConfig
import com.mcmouse88.okhttp.domain.accounts.entities.Account
import com.mcmouse88.okhttp.domain.accounts.entities.SignUpData
import com.mcmouse88.okhttp.test_utils.createAccount
import com.squareup.moshi.Moshi
import io.mockk.CapturingSlot
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class RetrofitAccountsSourceTest {

    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var accountsApi: AccountsApi


    /**
     * Данный тест проверяет, что метод правильно передает параметры в ретрофит интерфейс и отдает
     * наружу токен, который приходит от сервера.
     */
    @Test
    fun signInCallsEndpointAndReturnsToken() = runTest {
        val requestEntity = SignInRequestEntity("email", "password")
        coEvery { accountsApi.signIn(requestEntity) } returns
                SignInResponseEntity("token")

        val source = createSource()

        val token = source.signIn("email", "password")

        Assert.assertEquals("token", token)
        coVerify(exactly = 1) {
            accountsApi.signIn(requestEntity)
        }
        confirmVerified(accountsApi)
    }

    /**
     * Данный тест проверяет, что вызов данного метода происходит внутри метода
     * [wrapRetrofitException], который уже покрыт тестами на обработку ошибок при сетевых запросах.
     * В отличии от предыдущих тестов ммы создаем источник данных на базе шпиона с помощью метода
     * [spyk], а также создаем слот, который будет перехватывать блок кода, переданный внутрь
     * метода [wrapRetrofitException], при этом типы лямбды совпадают со слотом (только слот
     * возвращает строку, так как метод [signIn], который тестируем возвращает токен в виде строки,
     * а не дженерик. Дальше у источника вызываем метод signIn, но сам токен получем уже с
     * перехваченной слотом лямбды.
     */
    @Test
    fun signInWrapsExecutionIntoWrapRetrofitException() = runTest {
        val source = spyk(createSource())
        val requestEntity = SignInRequestEntity("email", "password")
        coEvery { accountsApi.signIn(requestEntity) } returns
                SignInResponseEntity("token")
        val slot: CapturingSlot<suspend () -> String> = slot()
        coEvery { source.wrapRetrofitException(capture(slot)) } returns ""

        source.signIn("email", "password")
        val token = slot.captured.invoke()

        Assert.assertEquals("token", token)
    }

    /**
     * Данный метод тоже проверяет, что тестируемый метод вызывается внутри лямбды метода
     * [wrapRetrofitException], однако так как тестируемый метод ничего не возвращает,
     * то проверка немного отличается, и после перехвата лямбды мы просто проверяем, что метод
     * был вызван с нужными параметрами:
     * ```kotlin
     * coVerify { accountsApi.signUp(requestEntity) }
     * ```
     */
    @Test
    fun signUpWrapsExecutionIntoWrapRetrofitException() = runTest {
        val inputEmail = "email"
        val inputUsername = "username"
        val inputPassword = "password"
        val requestEntity = SignUpRequestEntity(inputEmail, inputUsername, inputPassword)
        val source = spyk(createSource())
        coEvery { accountsApi.signUp(any()) } just runs
        val slot: CapturingSlot<suspend () -> Unit> = slot()
        coEvery { source.wrapRetrofitException(capture(slot)) } just runs

        val signUpData = SignUpData(inputUsername, inputEmail, inputPassword, inputPassword)
        source.signUp(signUpData)

        verify { accountsApi wasNot called }
        slot.captured.invoke()
        coVerify { accountsApi.signUp(requestEntity) }
    }

    @Test
    fun getAccountsCallEndpoint() = runTest {
        val source = createSource()
        val getAccountResponse = mockk<GetAccountResponseEntity>()
        val expectedAccount = Account(
            id = 1,
            username = "username",
            email = "email",
            createdAt = 123456789,
        )

        every { getAccountResponse.toAccount() } returns expectedAccount
        coEvery { accountsApi.getAccount() } returns getAccountResponse

        val account = source.getAccount()

        Assert.assertSame(expectedAccount, account)
        coVerify(exactly = 1) { accountsApi.getAccount() }
        confirmVerified(accountsApi)
    }

    @Test
    fun getAccountWrapsExecutionIntoWrapRetrofitExceptions() = runTest {
        val source = spyk(createSource())
        val getAccountResponse = mockk<GetAccountResponseEntity>()
        val expectedAccount = createAccount(
            id = 123, username = "username", email = "email"
        )
        every { getAccountResponse.toAccount() } returns expectedAccount
        coEvery { accountsApi.getAccount() } returns getAccountResponse
        val slot: CapturingSlot<suspend () -> Account> = slot()
        coEvery { source.wrapRetrofitException(capture(slot)) } returns createAccount()

        source.getAccount()
        val account = slot.captured.invoke()

        Assert.assertSame(expectedAccount, account)
    }

    @Test
    fun setUsernameCallsEndpoint() = runTest {
        val inputUsername = "username"
        val expectedRequestEntity = UpdateUsernameRequestEntity(inputUsername)
        val source = createSource()
        coEvery { accountsApi.setUserName(any()) } just runs

        source.setUsername(inputUsername)

        coVerify(exactly = 1) { accountsApi.setUserName(expectedRequestEntity) }
        confirmVerified(accountsApi)
    }

    @Test
    fun setUsernameWrapsExecutionIntoWrapRetrofitExceptions() = runTest {
        val inputUsername = "username"
        val expectedRequestEntity = UpdateUsernameRequestEntity(inputUsername)
        val source = spyk(createSource())
        coEvery { accountsApi.setUserName(any()) } just runs
        val slot: CapturingSlot<suspend () -> Unit> = slot()
        coEvery { source.wrapRetrofitException(capture(slot)) } just runs

        source.setUsername(inputUsername)
        verify { accountsApi wasNot called }
        slot.captured.invoke()
        coVerify { accountsApi.setUserName(expectedRequestEntity) }
    }

    private fun createSource() = RetrofitAccountsSource(
        config = RetrofitConfig(
            retrofit = createRetrofit(),
            moshi = Moshi.Builder().build()
        )
    )

    private fun createRetrofit(): Retrofit {
        val retrofit = mockk<Retrofit>()
        every { retrofit.create(AccountsApi::class.java) } returns accountsApi
        return retrofit
    }
}