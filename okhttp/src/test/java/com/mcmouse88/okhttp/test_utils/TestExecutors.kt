package com.mcmouse88.okhttp.test_utils

import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.ExecutorService

fun immediateExecutorService(): ExecutorService {
    val service = mockk<ExecutorService>()
    every { service.execute(any()) } answers {
        firstArg<Runnable>().run()
    }
    every { service.submit(any()) } answers {
        firstArg<Runnable>().run()
        mockk(relaxed = true)
    }
    return service
}