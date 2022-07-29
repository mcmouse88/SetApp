package com.mcmouse88.foundation.model.tasks.factories

import com.mcmouse88.foundation.model.tasks.AbstractTask
import com.mcmouse88.foundation.model.tasks.SynchronizedTask
import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.model.tasks.TaskListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class ExecutorServiceTaskFactory(
    private val executorService: ExecutorService
) : TasksFactory {

    override fun <T> createTask(body: TaskBody<T>): Task<T> {
        return SynchronizedTask(ExecutorServiceTask(body))
    }

    /**
     * класс [ExecutorService] занимается управление потоков
     */
    private inner class ExecutorServiceTask<T>(
        private val body: TaskBody<T>
    ) : AbstractTask<T>() {

        private var future: Future<*>? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            future = executorService.submit {
                executeBody(body, listener)
            }
        }

        override fun doCancel() {
            future?.cancel(true)
        }
    }
}