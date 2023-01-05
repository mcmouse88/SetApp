package com.mcmouse88.mock_k

import java.util.concurrent.Executor

class ResourceManager<R>(
    private val executor: Executor,
    private val errorHandler: ErrorHandler<R>
) {

    private val consumers = mutableListOf<Consumer<R>>()
    private var resource: R? = null
    private var destroyed = false

    fun setResource(resource: R) = synchronized(this) {
        if (destroyed) return@synchronized
        this.resource = resource
        var localConsumer: List<Consumer<R>>
        do {
            localConsumer = ArrayList(consumers)
            consumers.clear()
            localConsumer.forEach { consumer ->
                processResource(consumer, resource)
            }
        } while (consumers.isNotEmpty())
    }

    fun clearResource() = synchronized(this) {
        if (destroyed) return@synchronized
        this.resource = null
    }

    fun consumeResource(consumer: Consumer<R>) = synchronized(this) {
        if (destroyed) return@synchronized
        val resource = this.resource
        if (resource != null) {
            processResource(consumer, resource)
        } else {
            consumers.add(consumer)
        }
    }

    fun destroy() = synchronized(this) {
        destroyed = true
        consumers.clear()
        resource = null
    }

    private fun processResource(consumer: Consumer<R>, resource: R) {
        executor.execute {
            try {
                consumer.invoke(resource)
            } catch (e: Exception) {
                errorHandler.onError(e, resource)
            }
        }
    }
}
