package com.mcmouse88.catadapterespresso.robolectric.test_utils.rules

import com.mcmouse88.catadapterespresso.robolectric.test_utils.ImmediateExecutorService
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.Executors

/**
 * Это правило нужно для тех тестов, которые тем или иным способом взаимодействуют с [RecyclerView].
 * Здесь происходит помена Executors сервиса, который использует [DiffUtil]
 */
class ImmediateDiffUtilRule : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        mockkStatic(Executors::class)
        every { Executors.newFixedThreadPool(any()) } returns ImmediateExecutorService()
    }

    override fun finished(description: Description) {
        super.finished(description)
        unmockkStatic(Executors::class)
    }
}