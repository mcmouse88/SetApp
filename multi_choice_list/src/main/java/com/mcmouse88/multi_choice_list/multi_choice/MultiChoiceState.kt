package com.mcmouse88.multi_choice_list.multi_choice

/**
 * Интерфейс который хранит информацию о состоянии выделенных элементов.
 */
 interface MultiChoiceState<T> {


    /**
     * Количество выделенных элементов
     */
    val totalCheckedCount: Int

    /**
     * Метод возвращающий информацию о том, выделен ли текущий элемент или нет.
     */
    fun isChecked(item: T): Boolean
}