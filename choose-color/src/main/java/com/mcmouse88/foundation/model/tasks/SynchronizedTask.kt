package com.mcmouse88.foundation.model.tasks

import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher
import java.util.concurrent.atomic.AtomicBoolean

class SynchronizedTask<T>(
    private val task: Task<T>
) : Task<T> {

    @Volatile
    private var canceled: Boolean = false
    private var executed: Boolean = false

    /**
     * Флаг для определения слушателя, вызывался ли он ранее или нет
     */
    private var listenerCalled = AtomicBoolean(false)

    override fun await(): T {
        synchronized(this) {
            if (canceled) throw CanceledException("This task: $task was canceled")
            if (executed) throw IllegalStateException("Task: $task has been executed")
            executed = true
        }
        return task.await()
    }

    override fun cancel() = synchronized(this) {
        if (listenerCalled.compareAndSet(false, true)) {
            if (canceled) return
            canceled = true
            task.cancel()
        }
    }

    /**
     * так как присвоение значения [finalListener] выполняется небезопасно с точки зрения
     * потокобезопасности, несмотря на то, что это происходит в блоке synchronized, так как
     * два потока одновременно могут вызвать один и тот же слушатель, мы будем использовать условия
     * ее инициализации (поле [listenerCalled]), как тип атомарный boolean, и менять его значение
     * через метод [compareAndSet()], в который передается два параметра, первый текущее значение,
     * второй - новое значение.
     */
    override fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>) = synchronized(this) {
        if (canceled) return
        if (executed) throw IllegalStateException("Task: $task has been executed")
        executed = true

        val finalListener: TaskListener<T> = {
            if (listenerCalled.compareAndSet(false, true)) {
                if (!canceled) listener(it)
            }
        }

        task.enqueue(dispatcher, finalListener)
    }
}