package com.mcmouse88.catadapterespresso.robolectric.test_utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

/**
 * Вспомогательный файл для тестирования kotlin Flow
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun runFlowTest(
    testBody: suspend FlowTest.() -> Unit
) {
    runTest {
        val scope = FlowTest(this)
        testBody.invoke(scope)
        scope.cancelAll()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FlowTest(
    private val scope: TestScope
) {

    private val allJobs = mutableMapOf<Flow<*>, MutableList<Job>>()

    fun <T> Flow<T>.startCollecting(): List<T> {
        val list = mutableListOf<T>()
        val job = scope.launch(UnconfinedTestDispatcher()) {
            toList(list)
        }
        val jobs = allJobs[this] ?: mutableListOf<Job>().also {
            allJobs[this] = it
        }
        jobs.add(job)
        return list
    }

    fun <T> Flow<T>.cancelCollecting() {
        allJobs[this]?.forEach(Job::cancel)
        allJobs[this]?.clear()
    }

    fun cancelAll() {
        allJobs.forEach { it.key.cancelCollecting() }
    }
}