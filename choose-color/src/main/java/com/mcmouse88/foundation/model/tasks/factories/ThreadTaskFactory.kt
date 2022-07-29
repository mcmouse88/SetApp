package com.mcmouse88.foundation.model.tasks.factories

import com.mcmouse88.foundation.model.tasks.AbstractTask
import com.mcmouse88.foundation.model.tasks.SynchronizedTask
import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.model.tasks.TaskListener

class ThreadTaskFactory : TasksFactory {

    override fun <T> createTask(body: TaskBody<T>): Task<T> {
        return SynchronizedTask(ThreadTask(body))
    }

    private class ThreadTask<T>(
        private val body: TaskBody<T>
    ) : AbstractTask<T>() {

        private var thread: Thread? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            thread = Thread {
                executeBody(body, listener)
            }
            thread?.start()
        }

        override fun doCancel() {
            thread?.interrupt()
        }
    }
}