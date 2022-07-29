package com.mcmouse88.foundation.model.tasks.factories

import android.os.Handler
import android.os.HandlerThread
import com.mcmouse88.foundation.model.tasks.AbstractTask
import com.mcmouse88.foundation.model.tasks.SynchronizedTask
import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.model.tasks.TaskListener

class HandlerThreadTasksFactory : TasksFactory {

    /**
     * Для создания объекта класса [HandlerThread] ему нужно передать в конструктор параметр имя
     * типа String, используем имя нашего класса путем вызова [javaClass.simpleName]
     */
    private val thread = HandlerThread(javaClass.simpleName)

    init {
        thread.start()
    }

    /**
     * Создадим [Handler], только вместо [Looper] главного потока, передадим в параметр [Looper]
     * текущего потока
     */
    private val handler = Handler(thread.looper)
    private var destroyed = false

    override fun <T> createTask(body: TaskBody<T>): Task<T> {
        return SynchronizedTask(HandlerThreadTask(body))
    }

    fun close() {
        destroyed = true
        thread.quitSafely()
    }

    private inner class HandlerThreadTask<T>(
        private val body: TaskBody<T>
    ) : AbstractTask<T>() {

        private var thread: Thread? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            val runnable = Runnable {
                thread = Thread {
                    executeBody(body, listener)
                }
                thread?.start()
            }
            handler.post(runnable)
        }

        override fun doCancel() {
            thread?.interrupt()
        }
    }
}