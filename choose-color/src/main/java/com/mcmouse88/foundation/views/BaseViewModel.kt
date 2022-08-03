package com.mcmouse88.foundation.views

import androidx.lifecycle.*
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.utils.Event
import kotlinx.coroutines.*

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Для удобства работы с результатом создадим три typealias
 */
typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Базовый класс для всех ViewModel (кроме [MainViewModel], который содержив себе опциональный
 * метод [onResult()], на случай если фрагменту нужно получить результат.
 */
open class BaseViewModel : ViewModel() {

    /**
     * Созададим свою реализацию [viewModelScope], для того, чтобы отменять задачу при нажатии на
     * кнопку назад, и чтобы не было бага(при нажатии на кнопку назад, за несколько милисекунд
     * до выполнения задачи, задача успевает завершиться и схлопнуть экран). Для этой реализации
     * на потребуется переменная типа [CoroutineContext], которую мы получим путем сложения
     * диспатчера и [SupervisorJob]. Все job внутри корутин иерархически связаны между собой.
     * Соответственно если мы отменяем job родителя, то все потомки тоже отменяются, и наоборот,
     * если в какой либо job потомка случается ошибка, то эта потом передается вверх по всей
     * иерархии. Таким образом если использовать обычную [Job], а не [SupervisorJob], то может
     * случится такая ситуация, что если какая-либо корутина завершится с ошибкой, то потом на
     * Scope не сможем вызвать ни одн корутину, потому что родительская job будет в статусе error.
     */
    private val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    protected val myViewModelScope = CoroutineScope(coroutineContext)

    open fun onResult(result: Any) {

    }

    fun onBackPressed(): Boolean {
        clearViewModelScope()
        return false
    }

    fun<T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        myViewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Exception) {
                liveResult.postValue(ErrorResult(e))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearViewModelScope()
    }

    private fun clearViewModelScope() {
        myViewModelScope.cancel()
    }
}