package com.mcmouse88.foundation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

private sealed class Element<T>

/**
 * Подкласс для элемента с данными
 */
private class ItemElement<T>(
    val item: T
) : Element<T>()

/**
 * Подкласс с ошибкой
 */
private class ErrorElement<T>(
    val error: Throwable
) : Element<T>()

/**
 * Элемент который сообщает об еспешном завершении
 */
private class CompletedElement<T> : Element<T>()

/**
 * Создадим свой оператор (все операторы для flow являются extension функциями). Чтобы функцию
 * можно было вызывать на flow любого типа, она будет типизирована. Реализация метода следующая ->
 * мы берем исходный flow (то есть this) и преобразуем все элементы, которые он нам отдает в
 * [ItemElement], то есть помещаем приходящий элемент внутрь. При преобразовании явно укажем, что
 * преобразуем тип T в Element<T>.
 * ```kotlin
 * map<T, Element<T>>
 * ```
 * Далее нам нужно материализовать ошибку и успешное завершение. Когда у нас этот поток завершается
 * (после первого оператора map), то это событие завершения можно получить при помощи оператора
 * [onCompletion], в котором мы выдаем полученный элемент, но уже как [CompletedElement]. Таким
 * образом мы материализовали и успешное завершение исходного потока. Далее нужно материализовать
 * ошибку. Ошибку можно получить при помощи оператора [catch], который позволяет поймать ошибку,
 * и как-то ее обработать. Саму ошибку мы завернем в [ErrorElement], и с помощью метода [emit]
 * продолжим flow. После всех преобразований мы получили flow типа Element<T>. Значит, что мы
 * в обычных элементах можем получить как просто элементы (айтемы), ошибку, так и сообщение о том,
 * что исходный flow завершен. Далее мы уже сможем использовать оператор [shareIn]. После его
 * вызову мы получаем flow, который бесконечный, и который не выдает ошибки. Чтобы сделать flow
 * конечным, вызовем оператор [takeWhile], но проверять будем уже на то, что у нас текущий элемент
 * это [ItemElement], то есть будем выполнять flow пока не получим [ErrorElement] или
 * [CompletedElement]. Далее преобразуем элементы к [ItemElement] (они уже все на входе такого
 * типа, просто нужно явно это прописать, чтобы получить item). Чтобы получить ошибку, также
 * проверяем элементы на ошибки, и если элемент содержит ошибку, то выбросем исключение, иначе
 * просто возвращаем элемент без всяких изменений.
 */
fun <T> Flow<T>.finiteShareIn(coroutineScope: CoroutineScope): Flow<T> {
    return this
        .map<T, Element<T>> { item -> ItemElement(item) }
        .onCompletion { emit(CompletedElement()) }
        .catch { exception -> emit(ErrorElement(exception)) }
        .shareIn(coroutineScope, SharingStarted.Eagerly, 1)
        .map {
            if (it is ErrorElement) throw it.error
            return@map it
        }
        .takeWhile { element -> element is ItemElement }
        .map { (it as ItemElement).item }
}