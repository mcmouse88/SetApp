package com.mcmouse88.foundation.model.tasks

import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.FinalResult
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.model.tasks.dispatcher.Dispatcher
import com.mcmouse88.foundation.model.tasks.factories.TaskBody
import com.mcmouse88.foundation.utils.delegates.Await

abstract class AbstractTask<T> : Task<T> {

    private var finalResult by Await<FinalResult<T>>()

    final override fun await(): T {
        val wrapperListener: TaskListener<T> = {
            finalResult = it
        }
        doEnqueue(wrapperListener)
        try {
            when(val result = finalResult) {
                is ErrorResult -> throw result.exception
                is SuccessResult -> return result.data
            }
        } catch (e: Exception) {
            if (e is InterruptedException) {
                cancel()
                throw CanceledException(originException = e)
            } else {
                throw e
            }
        }
    }

    final override fun cancel() {
        finalResult = ErrorResult(CanceledException())
        doCancel()
    }

    final override fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>) {
        val wrappedListener: TaskListener<T> = {
            finalResult = it
            dispatcher.dispatch {
                listener(finalResult)
            }
        }
        doEnqueue(wrappedListener)
    }

    fun executeBody(taskBody: TaskBody<T>, listener: TaskListener<T>) {
        try {
            val data = taskBody()
            listener(SuccessResult(data))
        } catch (e: Exception) {
            listener(ErrorResult(e))
        }
    }

    abstract fun doEnqueue(listener: TaskListener<T>)

    abstract fun doCancel()
}