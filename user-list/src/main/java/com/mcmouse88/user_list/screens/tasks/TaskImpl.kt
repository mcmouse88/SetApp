package com.mcmouse88.user_list.screens.tasks

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * [executorService] тот сервис, который будет выполнять задачи ассинхронно
 */
private val executorService = Executors.newCachedThreadPool()
private val handler = Handler(Looper.getMainLooper())

/**
 * внутри интерфейса [Callable] мы можем описать любые действия, которые будут выполняться
 * в каком-то другом потоке. Когда мы даем сервису какой-то объект [Callable] на выполнение, то
 * он нам возвращает объект типа [Future], с помощью которого мы можем либо отменить задачу, либо
 * получить результат
 */
class TaskImpl<T>(
    private val callable: Callable<T>
) : Task<T> {

    private val future: Future<*>

    /**
     * Так как мы сразу же начинаем выполнение задачи в блоке init, то изначально присвоим
     * переменной result значение [PendingResult]
     */
    private var result: Result<T> = PendingResult()

    private var valueCallBack: CallBack<T>? = null
    private var errorCallBack: CallBack<Throwable>? = null

    /**
     * В блоке init запустим задачу на выполнение (метод [submit()] объекта класса [ExecutorService]
     */
    init {
        future = executorService.submit {
            result = try {
                SuccessResult(callable.call())
            } catch (e: Throwable) {
                ErrorResult(e)
            }
            notifyListeners()
        }
    }

    override fun onSuccess(callBack: CallBack<T>): Task<T> {
        valueCallBack = callBack
        notifyListeners()
        return this
    }

    override fun onError(callBack: CallBack<Throwable>): Task<T> {
        errorCallBack = callBack
        notifyListeners()
        return this
    }

    override fun cancel() {
        clear()
        future.cancel(true)
    }

    /**
     * Метод [get()] у объекта future заставляет ждать поток выполнения, то есть код перейдет
     * на следующую строчку после этого метода только после того, как блок выполнится ассинхронно,
     * в данном случае блок init, так как там мы запускаем выполнение у объекта future.
     */
    override fun await(): T {
        future.get()
        val result = this.result
        if (result is SuccessResult) return result.data
        else throw (result as ErrorResult).error
    }

    /**
     * на всякий случай выполнгяем данную функцию внутри [handler.post], чтобы все данные
     * гарантировано передавались слушателям в главном потоке
     */
    private fun notifyListeners() {
        handler.post {
            val result = this.result
            val callBack = this.valueCallBack
            val errorCallBack = this.errorCallBack
            if (result is SuccessResult && callBack != null) {
                callBack(result.data)
                clear()
            } else if (result is ErrorResult && errorCallBack != null) {
                errorCallBack.invoke(result.error)
                clear()
            }
        }
    }

    private fun clear() {
        valueCallBack = null
        errorCallBack = null
    }
}