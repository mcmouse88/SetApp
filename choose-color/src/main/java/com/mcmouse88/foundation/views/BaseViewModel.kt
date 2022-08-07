package com.mcmouse88.foundation.views

import androidx.lifecycle.*
import com.mcmouse88.foundation.model.ErrorResult
import com.mcmouse88.foundation.model.Result
import com.mcmouse88.foundation.model.SuccessResult
import com.mcmouse88.foundation.utils.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Для удобства работы с результатом создадим три typealias
 */
typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

typealias ResultFlow<T> = Flow<Result<T>>
typealias ResultMutableStateFlow<T> = MutableStateFlow<Result<T>>

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

    fun<T> into(stateFlow: MutableStateFlow<Result<T>>, block: suspend () -> T) {
        myViewModelScope.launch {
            try {
                stateFlow.value = SuccessResult(block())
            } catch (e: Exception) {
                stateFlow.value = ErrorResult(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        clearViewModelScope()
    }

    /**
     * Собственная реализация преобразования [SavedStateHandle] в [StateFlow], на момент записи
     * ролика у [SavedStateHandle] отсутствовала данная реализация, но сейчас вроде бы появилась,
     * поэтому пока оставлю этот метод на всякий случай
     */
    fun<T> SavedStateHandle.getStateFlowMyExtension(key: String, initialValue: T): MutableStateFlow<T> {
        val savedStateHandle = this
        val mutableFlow = MutableStateFlow(savedStateHandle[key] ?: initialValue)

        viewModelScope.launch {
            mutableFlow.collect {
                savedStateHandle[key] = it
            }
        }

        viewModelScope.launch {
            savedStateHandle.getLiveData<T>(key).asFlow().collect {
                mutableFlow.value = it
            }
        }
        return mutableFlow
    }

    private fun clearViewModelScope() {
        myViewModelScope.cancel()
    }
}