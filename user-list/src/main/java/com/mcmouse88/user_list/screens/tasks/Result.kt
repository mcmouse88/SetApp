package com.mcmouse88.user_list.screens.tasks

/**
 * Будем использовать sealed class, для того, чтобы можно было получить разный результат. У нас
 * есть общий класс [Result], который может в себе нести какие-то данные. У нас есть
 * [SuccessResult], на случай если операция завершилась успешно, и она содержит в себе какие-то
 * данные. Также есть [ErrorResult] который будет содержать [Exception] с ошибкой, на случай
 * если операция завершилась с ошибкой. Также есть [PendingResult], который говорит, что задача еще
 * не завершилась, и она в процессе выполнения. Ну и последний [EmptyResult], который говорит
 * нам о том, что результата еще никакого нет, и задача еще даже не запускалась.
 */
sealed class Result<T> {

    @Suppress("UNCHECKED_CAST")
    fun<R> map(mapper: (T) -> R): Result<R> {
        if (this is SuccessResult) return SuccessResult(mapper(data))
        return this as Result<R>
    }
}

class SuccessResult<T>(
    val data: T
) : Result<T>()

class ErrorResult<T>(
    val error: Throwable
) : Result<T>()

class PendingResult<T> : Result<T>()

class EmptyResult<T> : Result<T>()
