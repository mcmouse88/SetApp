package com.mcmouse88.foundation

import android.content.Context
import androidx.annotation.MainThread

/**
 * Наша собственная реализация глобального синглтона, для инициализации глобальных библиотек (таких
 * как dagger, firebase и т.п.), то есть замена класса [Application]. Зачем заменять класс
 * [Application] собственной реализацией, так как с появлением версии Android 6, появилась
 * возможность запускать приложеие в Restricted Mode, при запуске которого используется
 * базовый класс Application, а пользовательская реализация, даже несмотря на то, что она прописана
 * в Manifest игнорируется, в результате чего можно получить NullPointerException и
 * ClassCastException, но запуск приложения в таком режиме довольно редкая ситуация, в связи
 * с чем инициализирование глобальных сущностей в классе [Application] все равно производится, в том
 * числе даже крупными компаниями, игнорируя возможные ошибки при запуске приложения в режиме
 * Restricted Mode. Аннотация [MainThread] говорит о том, что код будет выполняться в главном
 * потоке
 */

typealias SingletonFactory = (applicationContext: Context) -> List<Any>

object SingletonScopeDependencies {

    private var factory: SingletonFactory? = null
    private var dependencies: List<Any>? = null

    @MainThread
    fun init(factory: SingletonFactory) {
        if (this.factory != null) return
        this.factory = factory
    }

    @MainThread
    fun getDependencies(applicationContext: Context): List<Any> {
        val factory = this.factory
            ?: throw IllegalStateException("Call init() before getting singleton dependencies")
        return dependencies ?: factory.invoke(applicationContext).also { this.dependencies = it }
    }
}