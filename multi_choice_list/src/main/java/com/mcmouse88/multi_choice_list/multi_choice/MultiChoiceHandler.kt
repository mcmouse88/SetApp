package com.mcmouse88.multi_choice_list.multi_choice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Сущность, которая управляет множественным выбором
 */
interface MultiChoiceHandler<T : Any> {

    /**
     * Назначает данному интерфейсу специальный флоу, что позволяет слушать конкретные изменения в
     * списке
     */
    fun setItemFlow(coroutineScope: CoroutineScope, itemsFlow: Flow<List<T>>)

    /**
     * Метод возвращает фолу с текущим состоянием выделенных элементов списка
     */
    fun listen(): Flow<MultiChoiceState<T>>

    fun toggle(item: T)

    fun selectAll()

    fun clearAll()

    fun check(item: T)

    fun clear(item: T)
}