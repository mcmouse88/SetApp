package com.mcmouse88.okhttp.data.boxes

import android.graphics.Color
import com.mcmouse88.okhttp.data.base.RetrofitConfig
import com.mcmouse88.okhttp.data.boxes.entity.GetBoxResponseEntity
import com.mcmouse88.okhttp.data.boxes.entity.UpdateBoxRequestEntity
import com.mcmouse88.okhttp.domain.AppException
import com.mcmouse88.okhttp.domain.BackendException
import com.mcmouse88.okhttp.domain.ConnectionException
import com.mcmouse88.okhttp.domain.ParseBackendResponseException
import com.mcmouse88.okhttp.domain.boxes.entities.Box
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.test_utils.arranged
import com.mcmouse88.okhttp.test_utils.catch
import com.mcmouse88.okhttp.test_utils.wellDone
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import okio.IOException
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class RetrofitBoxesSourceTest {

    @get:Rule
    val rule = MockKRule(this)

    @RelaxedMockK
    lateinit var boxesApi: BoxesApi

    private lateinit var retrofitBoxesSource: RetrofitBoxesSource

    @Before
    fun setUp() {
        retrofitBoxesSource = createRetrofitBoxesSource()
    }

    @Test
    fun getBoxesWithOnlyActiveBoxesFilterInvokesEndpoint() = runTest {
        val filter = BoxesFilter.ONLY_ACTIVE
        retrofitBoxesSource.getBoxes(filter)

        coVerify(exactly = 1) { boxesApi.getBoxes(true) }
        confirmVerified(boxesApi)
    }

    @Test
    fun getBoxesWithAllBoxesFilterInvokesEndpoint() = runTest {
        val filter = BoxesFilter.ALL
        retrofitBoxesSource.getBoxes(filter)

        coVerify(exactly = 1) { boxesApi.getBoxes(isNull()) }
        confirmVerified(boxesApi)
    }

    @Test
    fun getBoxesReturnsBoxesFromEndpoints() = runTest {
        val expectedBox1 = BoxAndSettings(
            box = Box(
                id = 1,
                colorName = "Red",
                colorValue = Color.RED
            ),
            isActive = true
        )

        val expectedBox2 = BoxAndSettings(
            box = Box(
                id = 3,
                colorName = "Green",
                colorValue = Color.GREEN
            ),
            isActive = false
        )

        val box1Response = mockk<GetBoxResponseEntity>()
        val box2Response = mockk<GetBoxResponseEntity>()
        every { box1Response.toBoxAndSettings() } returns expectedBox1
        every { box2Response.toBoxAndSettings() } returns expectedBox2

        coEvery { boxesApi.getBoxes(any()) } returns listOf(box1Response, box2Response)
        val boxes = retrofitBoxesSource.getBoxes(BoxesFilter.ALL)

        Assert.assertEquals(boxes.size, 2)
        Assert.assertEquals(boxes[0], expectedBox1)
        Assert.assertEquals(boxes[1], expectedBox2)
    }

    @Test
    fun getBoxesWithAppExceptionRethrowsException() = runTest {
        val expectedException = AppException()
        coEvery { boxesApi.getBoxes(any()) } throws expectedException

        val exception: AppException = catch { retrofitBoxesSource.getBoxes(BoxesFilter.ALL) }
        Assert.assertSame(expectedException, exception)
    }

    @Test
    fun getBoxesWithJsonDataExceptionThrowsParseBackendResponseException() = runTest {
        coEvery { boxesApi.getBoxes(any()) } throws JsonDataException()
        catch<ParseBackendResponseException> { retrofitBoxesSource.getBoxes(BoxesFilter.ALL) }
        wellDone()
    }

    @Test
    fun getBoxesWithJsonEncodingExceptionThrowsParseBackendResponseException() = runTest {
        coEvery { boxesApi.getBoxes(any()) } throws JsonEncodingException("oops")
        catch<ParseBackendResponseException> { retrofitBoxesSource.getBoxes(BoxesFilter.ALL) }
        wellDone()
    }

    @Test
    fun getBoxesWithIOExceptionThrowsConnectionException() = runTest {
        coEvery { boxesApi.getBoxes(any()) } throws IOException()
        catch<ConnectionException> { retrofitBoxesSource.getBoxes(BoxesFilter.ALL) }
        wellDone()
    }

    @Test
    fun getBoxesWithHttpExceptionThrowsBackendResponseException() = runTest {
        val httpException = mockk<HttpException>()
        val response = mockk<Response<*>>()
        val errorBody = mockk<ResponseBody>()
        val errorJson = "{\"error\": \"Oops\"}"
        coEvery { boxesApi.getBoxes(any()) } throws httpException
        every { httpException.response() } returns response
        every { httpException.code() } returns 409
        every { response.errorBody() } returns errorBody
        every { errorBody.string() } returns errorJson

        val exception: BackendException = catch { retrofitBoxesSource.getBoxes(BoxesFilter.ALL) }

        Assert.assertEquals("Oops", exception.message)
        Assert.assertEquals(409, exception.code)
    }

    @Test
    fun setIsActiveCallsEndpoints() = runTest {
        arranged()

        retrofitBoxesSource.setIsActive(2, true)
        coVerify(exactly = 1) { boxesApi.setIsActive(2, UpdateBoxRequestEntity(true)) }
        confirmVerified(boxesApi)
    }

    @Test
    fun setIsActiveWithAppExceptionRethrowsException() = runTest {
        val expectedException = AppException()
        coEvery { boxesApi.setIsActive(any(), any()) } throws expectedException

        val exception: AppException = catch { retrofitBoxesSource.setIsActive(1, true) }
        Assert.assertSame(expectedException, exception)
    }

    @Test
    fun setIsActiveWithJsonDataExceptionThrowsParseBackendResponseException() = runTest {
        coEvery { boxesApi.setIsActive(any(), any()) } throws JsonDataException()
        catch<ParseBackendResponseException> { retrofitBoxesSource.setIsActive(1, true) }
        wellDone()
    }

    @Test
    fun setIsActiveWithJsonEncodingExceptionThrowsParseBackendResponseException() = runTest {
        coEvery { boxesApi.setIsActive(any(), any()) } throws JsonEncodingException("Oops")
        catch<ParseBackendResponseException> { retrofitBoxesSource.setIsActive(1, true) }
        wellDone()
    }

    @Test
    fun setIsActiveWithIOExceptionThrowsConnectionException() = runTest {
        coEvery { boxesApi.setIsActive(any(), any()) } throws IOException()
        catch<ConnectionException> { retrofitBoxesSource.setIsActive(1, true) }
        wellDone()
    }

    @Test
    fun setIsActiveWithHttpExceptionThrowsBackendException() = runTest {
        val httpException = mockk<HttpException>()
        val response = mockk<Response<*>>()
        val errorBody = mockk<ResponseBody>()
        val errorJson = "{\"error\": \"Oops\"}"
        coEvery { boxesApi.setIsActive(any(), any()) } throws httpException
        every { httpException.response() } returns response
        every { httpException.code() } returns 409
        every { response.errorBody() } returns errorBody
        every { errorBody.string() } returns errorJson

        val exception: BackendException = catch { retrofitBoxesSource.setIsActive(1, true) }
        Assert.assertEquals("Oops", exception.message)
        Assert.assertEquals(409, exception.code)
    }

    private fun createRetrofitBoxesSource(
        retrofit: Retrofit = createRetrofit(),
        moshi: Moshi = createMoshi()
    ): RetrofitBoxesSource {
        val config = RetrofitConfig(
            retrofit = retrofit,
            moshi = moshi
        )
        return RetrofitBoxesSource(config)
    }

    private fun createRetrofit(): Retrofit {
        val retrofit = mockk<Retrofit>()
        every { retrofit.create(BoxesApi::class.java) } returns boxesApi
        return retrofit
    }

    private fun createMoshi(): Moshi = Moshi.Builder().build()
}