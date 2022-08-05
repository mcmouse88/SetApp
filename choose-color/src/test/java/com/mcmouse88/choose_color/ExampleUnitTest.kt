package com.mcmouse88.choose_color

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testFlow() = runBlocking {

        /**
         * Метод [flowOf()] создает Flow в случае если уже заранее известны элементы, которые Flow
         * будет испускать. Данный Flow является конечным, потому что в него передано определенное
         * количество числе, и данный Flow является холодным, то есть не будет испускать элементы
         * пока на нем не будет вызван терминальный оператор (в данном случае терминальный оператор
         * это метод [collect()]). Так как Flow холодный, то второй вызов метода [collect()]
         * запустит то же самое содержимое то есть
         * ```kotlin
         * flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
         * ```
         *
         */
        val flow: Flow<Int> = flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        /**
         * Запись аналогичная записи выше то есть содержимое flow1 будет как у первого flow, метод
         * [asFlow()] можно вызвать у любых объектов реализующих интерфейс [Iterable]
         */
        val numbers: IntRange = 1..10
        val flow1 = numbers.asFlow()

        println("Printing only even numbers multiplied by 10: ")

        flow
            .filter { it % 2 == 0 }
            .map { it * 10 }
            .collect {
                println(it)
            }

        println("Printing only odd number: ")
        flow
            .filter { it % 2 == 1 }
            .collect {
                println(it)
            }
    }
}