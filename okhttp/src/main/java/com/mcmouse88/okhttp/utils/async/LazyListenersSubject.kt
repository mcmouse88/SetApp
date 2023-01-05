package com.mcmouse88.okhttp.utils.async

import com.mcmouse88.okhttp.domain.Empty
import com.mcmouse88.okhttp.domain.Error
import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

typealias ValueLoader<A, T> = (A) -> T?
typealias ValueListener<T> = (ResultResponse<T>) -> Unit

class LazyListenersSubject<A : Any, T : Any>(
    private val loaderExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
    private val handlerExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
    private val loader: ValueLoader<A, T>
) {

    private val listeners = mutableListOf<ListenerRecord<A, T>>()
    private val futures = mutableMapOf<A, FutureRecord<T>>()

    fun addListener(argument: A, listener: ValueListener<T>) = handlerExecutor.execute {
        val listenerRecord = ListenerRecord(argument, listener)
        listeners.add(listenerRecord)
        val futureRecord = futures[argument]
        if (futureRecord == null) {
            execute(argument)
        } else {
            listener.invoke(futureRecord.lastValue)
        }
    }

    fun removeListener(argument: A, listener: ValueListener<T>) = handlerExecutor.execute {
        listeners.removeAll { it.listener == listener && it.arg == argument }
        if (listeners.any { it.arg == argument }.not()) {
            cancel(argument)
        }
    }

    fun reloadAll(silentMode: Boolean = false) = handlerExecutor.execute {
        futures.forEach { entry ->
            val argument = entry.key
            val record = entry.value
            restart(argument, record, silentMode)
        }
    }

    fun reloadArgument(argument: A, silentMode: Boolean = false) = handlerExecutor.execute {
        val record = futures[argument] ?: return@execute
        restart(argument, record, silentMode)
    }

    fun updateAllValues(newValue: T?) {
        futures.forEach {
            val result = if (newValue == null) Empty() else Success(newValue)
            it.value.lastValue = result
            publish(it.key, result)
        }
    }

    private fun execute(argument: A, silentMode: Boolean = false) {
        val record = FutureRecord<T>(null, Pending())
        futures[argument] = record
        val future = loaderExecutor.submit {
            try {
               if (silentMode.not()) publishIfNotCancelled(record, argument, Pending())
                val res = loader(argument)
                if (res == null) {
                    publishIfNotCancelled(record, argument, Empty())
                } else {
                    publishIfNotCancelled(record, argument, Success(res))
                }
            } catch (e: Exception) {
                publishIfNotCancelled(record, argument, Error(e))
            }
        }
        record.future = future
    }

    private fun publishIfNotCancelled(record: FutureRecord<T>, argument: A, result: ResultResponse<T>) {
        if (record.cancelled) return
        publish(argument, result)
    }

    private fun publish(argument: A, result: ResultResponse<T>) = handlerExecutor.execute {
        futures.filter { it.key == argument }.forEach {
            it.value.lastValue = result
        }
        listeners.filter { it.arg == argument }.forEach {
            it.listener.invoke(result)
        }
    }

    private fun cancel(argument: A) {
        val record = futures[argument]
        if (record != null) {
            futures.remove(argument)
            record.future?.cancel(true)
        }
    }

    private fun restart(argument: A, record: FutureRecord<T>, silentMode: Boolean) {
        record.cancelled = true
        record.future?.cancel(true)
        execute(argument, silentMode)
    }

    private class ListenerRecord<A, T>(
        val arg: A,
        val listener: ValueListener<T>
    )

    private class FutureRecord<T>(
        var future: Future<*>?,
        var lastValue: ResultResponse<T> = Empty(),
        var cancelled: Boolean = false
    )
}