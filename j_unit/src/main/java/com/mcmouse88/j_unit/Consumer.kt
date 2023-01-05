package com.mcmouse88.j_unit

/**
 * Resource consumer.
 * Usage example:
 * ```
 *     val consumer: Consumer<String> = { string ->
 *         println(string)
 *     }
 *     resourceManager.consumeResource(consumer)
 * ```
 */
typealias Consumer<R> = (R) -> Unit