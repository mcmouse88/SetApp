package com.mcmouse88.okhttp.utils.async

import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.test_utils.immediateExecutorService
import com.mcmouse88.okhttp.test_utils.runFlowTest
import io.mockk.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

class DefaultLazyFlowFactoryTest {

    @Before
    fun setUp() {
        mockkStatic(Executors::class)
        every { Executors.newSingleThreadExecutor() } returns immediateExecutorService()
    }

    @After
    fun tearDown() {
        unmockkStatic(Executors::class)
    }

    @Test
    fun createLazyFlowSubject() = runFlowTest {
        val factory = DefaultLazyFlowFactory(DefaultLazyListenersFactory())
        val loader: SuspendValueLoader<String, String> = mockk()
        coEvery { loader("arg") } returns "result"

        val subject: LazyFlowSubject<String, String> = factory.createLazyFlowSubject(loader)
        val collectedResult = subject.listen("arg").startCollecting()

        Assert.assertEquals(
            listOf(Pending(), Success("result")),
            collectedResult
        )
    }
}