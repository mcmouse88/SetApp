package com.mcmouse88.foundation.utils.delegates

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

/**
 * Делегат, предназанченный для того, что если при попытке чтения данных из переменной, которую
 * делегировали данному классу, в ней оказалось значение null, поток был заблокирован, и ждал пока
 * в переменной появится результат, кроме того значение при делегировании будет записано только
 * один раз, а все последующие попытки изменения будут игнорироваться. [CountDownLatch] это
 * еще один способ работы с потоками, при вызове метода [getValue] проверяется, что если значение
 * счетчика не равно 0 (по умолчанию оно 1, и декрементируется только после присвоения значения),
 * то поток будет ждать(метод [await()]) пока значение счетчика не станет равно 0. При присвоении значение при
 * вызове метода [countDown()] значение счетчика декрементируется на 1. Если же на момент вызова
 * метода [countDown()] счетчик уже равен, то значение уже уменьшаться не будет.
 */
class Await<T> {

    private val countDownLatch = CountDownLatch(1)
    private val value = AtomicReference<T>(null)

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        countDownLatch.await()
        return value.get()
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (value == null) return
        if (this.value.compareAndSet(null, value)) {
            countDownLatch.countDown()
        }
    }
}