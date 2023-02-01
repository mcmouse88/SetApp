package com.mcmouse88.catadapterespresso.robolectric.test_utils

import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Класс, который будет подменять настоящий ExecutorService, выполняет все отправленные ему
 * ассинхронные задачи сразу, а не ассинхронно, это нужно для нормальной работы с [RecyclerView].
 */
class ImmediateExecutorService : ExecutorService {

    private var cancelled = false

    override fun execute(command: Runnable) {
        command.run()
    }

    override fun shutdown() {
        cancelled = true
    }

    override fun shutdownNow(): MutableList<Runnable> {
        return ArrayList()
    }

    override fun isShutdown(): Boolean {
        return cancelled
    }

    override fun isTerminated(): Boolean {
        return cancelled
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit?): Boolean {
        return true
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        val future = mockk<Future<T>>(relaxed =true)
        try {
            val result = task.call()
            every { future.get() } returns result
            every { future.get(any(), any()) } returns result
        } catch (e: Exception) {
            every { future.get() } throws ExecutionException(e)
        }
        return future
    }

    override fun <T : Any?> submit(task: Runnable?, result: T): Future<T> {
        return submit(Callable { result })
    }

    override fun submit(task: Runnable): Future<*> {
        task.run()
        return mockk(relaxed = true)
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>?): MutableList<Future<T>> {
        throw NotImplementedError()
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>?,
        timeout: Long,
        unit: TimeUnit?
    ): MutableList<Future<T>> {
        throw NotImplementedError()
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>?): T {
        throw NotImplementedError()
    }

    override fun <T : Any?> invokeAny(
        tasks: MutableCollection<out Callable<T>>?,
        timeout: Long,
        unit: TimeUnit?
    ): T {
        throw NotImplementedError()
    }
}