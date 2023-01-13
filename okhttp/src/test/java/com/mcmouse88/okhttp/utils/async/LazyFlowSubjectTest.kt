package com.mcmouse88.okhttp.utils.async

import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.test_utils.immediateExecutorService
import com.mcmouse88.okhttp.test_utils.runFlowTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import java.util.concurrent.Executors

@OptIn(ExperimentalCoroutinesApi::class)
class LazyFlowSubjectTest {

    @get:Rule
    val rule = MockKRule(this)

    @RelaxedMockK
    lateinit var lazyListenersSubject: LazyListenersSubject<String, String>

    @MockK
    lateinit var lazyListenerFactory: LazyListenersFactory

    lateinit var loader: SuspendValueLoader<String, String>

    lateinit var lazyFlowSubject: LazyFlowSubject<String, String>

    @Before
    fun setUp() = runTest {
        loader = mockk(relaxed = true)
        every {
            lazyListenerFactory.createLazyListenersSubject<String, String>(any(), any(), any())
        } returns lazyListenersSubject

        lazyFlowSubject = LazyFlowSubject(lazyListenerFactory, loader)

        mockkStatic(Executors::class)
        every { Executors.newSingleThreadExecutor() } returns immediateExecutorService()
    }

    @After
    fun tearDown() {
        unmockkStatic(Executors::class)
    }

    @Test
    fun initCreatesLazyListenerSubjectWithValidLoader() {
        val slot: CapturingSlot<ValueLoader<String, String>> = slot()
        every {
            lazyListenerFactory.createLazyListenersSubject(any(), any(), capture(slot))
        } returns mockk()

        coEvery { loader("arg") } returns "result"

        LazyFlowSubject(lazyListenerFactory, loader)
        val answer = slot.captured("arg")
        Assert.assertEquals("result", answer)
    }

    @Test
    fun reloadAllDelegatesCallToLazyListenerSubject() {
        lazyFlowSubject.reloadAll(silentMode = true)
        verify(exactly = 1) {
            lazyListenersSubject.reloadAll(silentMode = true)
        }
    }

    @Test
    fun reloadAllDoesNotUseSilentModeByDefault() {
        lazyFlowSubject.reloadAll()
        verify(exactly = 1) {
            lazyListenersSubject.reloadAll(silentMode = false)
        }
    }

    @Test
    fun reloadArgumentDelegatesCallToLazyListenerSubject() {
        lazyFlowSubject.reloadArgument("test")
        verify(exactly = 1) {
            lazyListenersSubject.reloadArgument("test")
        }
    }

    @Test
    fun updateAllValuesDelegatesCallToLazyListenerSubject() {
        lazyFlowSubject.updateAllValues("test")
        verify(exactly = 1) {
            lazyListenersSubject.updateAllValues("test")
        }
    }

    @Test
    fun listenDeliversResultsFromCallbackToFlow() = runFlowTest {
        val slot = captureAddListener("arg")
        val flow = lazyFlowSubject.listen("arg")
        val result = flow.startCollecting()
        slot.captured(Pending())
        slot.captured(Success("hi"))

        Assert.assertEquals(
            listOf(Pending(), Success("hi")),
            result
        )
    }

    @Test
    fun listenAfterCancellingSubscriptionRemovesCallback() = runFlowTest {
        val slot = captureAddListener("arg")
        val flow = lazyFlowSubject.listen("arg")
        val result = flow.startCollecting()

        slot.captured(Success("111"))
        flow.cancelCollecting()
        slot.captured(Success("222"))

        Assert.assertEquals(
            listOf(Success("111")),
            result
        )
        verify(exactly = 1) {
            lazyListenersSubject.removeListener("arg", refEq(slot.captured))
        }
    }

    private fun captureAddListener(arg: String): CapturingSlot<ValueListener<String>> {
        val slot: CapturingSlot<ValueListener<String>> = slot()
        every { lazyListenersSubject.addListener(arg, capture(slot)) } just runs
        return slot
    }
}