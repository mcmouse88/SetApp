package com.mcmouse88.okhttp.utils.async

import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.test_utils.immediateExecutorService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DefaultLazyListenersFactoryTest {

    @Test
    fun createLazyListenerSubject() {
        val factory = DefaultLazyListenersFactory()
        val loader: ValueLoader<String, String> = mockk()
        val listener: ValueListener<String> = mockk(relaxed = true)
        every { loader("arg") } returns "result"

        val subject: LazyListenersSubject<String, String> = factory.createLazyListenersSubject(
            loaderExecutor = immediateExecutorService(),
            handlerExecutor = immediateExecutorService(),
            loader = loader
        )
        subject.addListener("arg", listener)
        verify { listener(Success("result")) }
    }
}