package com.mcmouse88.foundation.model.tasks.callback

import com.mcmouse88.foundation.model.FinalResult
import com.mcmouse88.foundation.model.tasks.AbstractTask
import com.mcmouse88.foundation.model.tasks.SynchronizedTask
import com.mcmouse88.foundation.model.tasks.Task
import com.mcmouse88.foundation.model.tasks.TaskListener

class CallbackTask<T> private constructor(
    private val executionListener: ExecutionListener<T>
) : AbstractTask<T>() {

    private var emitter: EmitterImpl<T>? = null

    override fun doEnqueue(listener: TaskListener<T>) {
        emitter = EmitterImpl(listener).also { executionListener(it) }
    }

    override fun doCancel() {
        emitter?.onCancelListener?.invoke()
    }

    private class EmitterImpl<T>(
        private val taskListener: TaskListener<T>
    ) : Emitter<T> {

        var onCancelListener: CancelListener? = null

        override fun emit(finalResult: FinalResult<T>) {
            taskListener.invoke(finalResult)
        }

        override fun setCancelListener(cancelListener: CancelListener) {
            this.onCancelListener = cancelListener
        }
    }

    companion object {
        fun<T> create(executionListener: ExecutionListener<T>): Task<T> {
            return SynchronizedTask(CallbackTask(executionListener))
        }
    }
}