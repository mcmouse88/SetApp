package com.mcmouse88.okhttp.data.base

import com.mcmouse88.okhttp.domain.AppException
import com.mcmouse88.okhttp.domain.BackendException
import com.mcmouse88.okhttp.domain.ConnectionException
import com.mcmouse88.okhttp.domain.ParseBackendResponseException
import com.mcmouse88.okhttp.test_utils.catch
import com.mcmouse88.okhttp.test_utils.wellDone
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import okio.IOException
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class BaseRetrofitSourceTest {

    @Test
    fun getRetrofitReturnsInstanceFromConfig() {
        val expectedRetrofit = mockk<Retrofit>()
        val source = createBaseRetrofitSource(
            retrofit = expectedRetrofit
        )

        val retrofit = source.retrofit
        Assert.assertSame(expectedRetrofit, retrofit)
    }

    /**
     * Так как в тестах нельзя применять suspend функции, и CoroutineScope там тоже взяться особо
     * не откуда, то тестировать suspend функции можно либо при помощи вызова [runBlocking], но
     * в таком случае если в тестируемой функции присутствуют искуственные задержки, то и сам тест
     * тоже будет выполняться с задержкой. Вместо [runBlocking] лучше использовать функцию,
     * написанную специально для тестирования [runTest], которая будет работать также как и
     * работала [runBlocking] за исключением того, что она будет игнорировать исскуственные
     * задержки. В данный момент метод является эксперементальным. Чтобы им можно было пользоваться
     * необходимо подключить зависимость:
     * ```groovy
     * testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1"
     * ```
     *
     * Также при работе с корутинами к обычным тестовым методам необходимо добавлять приставку co,
     * например [every] -> [coEvery] и т.д.
     * При тестировании данной функции, мы создаем переменную для источника получения данных (в
     * данном случае [BaseRetrofitSource]), а также создаем переменную для блока suspend кода,
     * который будет возвращать некую строку, дальше у источника получения данных вызываем метод,
     * в который передаем наш блок кода, и проверяем что ошибок нет и блок кода вернул нам
     * ожидаемую строку.
     */
    @Test
    fun wrapRetrofitExceptionsReturnsValueGeneratedByBlock() = runTest {
        val source = createBaseRetrofitSource()
        val block = createdMockedBlock()
        coEvery { block() } returns "test"

        val result = source.wrapRetrofitException(block)
        Assert.assertEquals("test", result)
    }

    /**
     * В этом тесте мы проверяем, что источник данных бросает ожидаемое нами исключение
     */
    @Test
    fun wrapRetrofitExceptionsWithAppExceptionsRethrowsException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createdMockedBlock()
        val expectedException = AppException()
        coEvery { block() } throws expectedException

        val exception: AppException = catch { source.wrapRetrofitException(block) }
        Assert.assertSame(expectedException, exception)
    }

    /**
     * В этом методе мы также проверяем, что источник данных бросает исключение, которое мы
     * обрабатываем и преобразуем в свое созданное исключение [ParseBackendResponseException].
     * Метод [wellDone] это просто маркер, что тест завершился успешно, и он ничего не делает,
     * данный маркер добавлять необязательно.
     */
    @Test
    fun wrapRetrofitExceptionsWithJsonDataExceptionThrowsParseBackendResponseException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createdMockedBlock()
        coEvery { block.invoke() } throws JsonDataException()

        catch<ParseBackendResponseException> { source.wrapRetrofitException(block) }
        wellDone()
    }

    @Test
    fun wrapRetrofitExceptionsWithJsonEncodingExceptionThrowsParseBackendResponseException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createdMockedBlock()
        coEvery { block.invoke() } throws JsonEncodingException("Bo-o-om")

        catch<ParseBackendResponseException> { source.wrapRetrofitException(block) }
        wellDone()
    }

    @Test
    fun wrapRetrofitExceptionsWithIOExceptionThrowsConnectionsException() = runTest {
        val source = createBaseRetrofitSource()
        val block = createdMockedBlock()
        coEvery { block.invoke() } throws IOException()

        catch<ConnectionException> { source.wrapRetrofitException(block) }
        wellDone()
    }

    /**
     * В этом тесте также как и в предыдущих проверяем на возможность получения [HttpException],
     * из которого получаем код ошибки и сообщение от сервера, также при помощи [mockk] мы можем
     * создавать не только абстрактные и свои классы, но и любые классы из любых библиотек, в том
     * числе и помеченные как final.
     */
    @Test
    fun wrapRetrofitExceptionsWithHttpExceptionThrowsBackendExceptions() = runTest {
        val source = createBaseRetrofitSource()
        val block = createdMockedBlock()
        val httpException = mockk<HttpException>()
        val response = mockk<Response<*>>()
        val errorBody = mockk<ResponseBody>()
        val errorJson = "{\"error\": \"Oops\"}"
        coEvery { block.invoke() } throws httpException
        every { httpException.response() } returns response
        every { httpException.code() } returns 409
        every { response.errorBody() } returns errorBody
        every { errorBody.string() } returns errorJson

        val exception: BackendException = catch {
            source.wrapRetrofitException(block)
        }

        Assert.assertEquals("Oops", exception.message)
        Assert.assertEquals(409, exception.code)
    }

    private fun createdMockedBlock(): suspend () -> String {
        return mockk()
    }

    private fun createBaseRetrofitSource(
        retrofit: Retrofit = mockk()
    ) = BaseRetrofitSource(
        RetrofitConfig(
            retrofit = retrofit,
            moshi = Moshi.Builder().build()
        )
    )
}