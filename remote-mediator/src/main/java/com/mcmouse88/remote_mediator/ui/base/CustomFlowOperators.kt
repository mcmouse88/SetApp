package com.mcmouse88.remote_mediator.ui.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan

fun<T> Flow<T>.simpleScan(count: Int): Flow<List<T?>> {
    val items = List<T?>(count) { null }
    return this.scan(items) { previous, value ->
        previous.drop(1) + value
    }
}