package com.mcmouse88.foundation.model

import java.lang.Exception

/**
 * Маппер для преобразования результата, у которого два типа, входящий и исходящий, это лямбла,
 * которая будет преобразовывать взодящий тип в исходящий.
 */
typealias ResultMapper<Input, OutPut> = (Input) -> OutPut

/**
 * sealed class в Котлин это классы, у которых все наследники уже будут известные на этапе
 * компиляци, и поэтому с ними более удобно работать. Класс [Result] у нас будет типизирован,
 * что это за тип пока неизвестно, поэтому он будет любым. Далее объявим его подклассы, то есть
 * возможные состояния. У результата будет три состояния, [PendingResult] - это озночает, что
 * результат еще находится в процессе выполнения, [SuccessResult] - процесс был выполнен успешно,
 * также у этого класса есть поле, которое будет содержать в себе данные, которые будут
 * получены в результате успешного выполнения операции, и [ErrorResult] - ошибка при получении
 * результата, также содержит в себе поле, в котором находятся данные об ощибки во время
 * выполнения результата. Также в данном классе будет extension функция, которая будет возвращать
 * данные полученные в результате успешного выполнения операции (SuccessResult). Этот метод
 * позволит нам проверить результат на успешность, а такде получить данные из класса [Result]
 */
sealed class Result<T> {

    /**
     * Функция, которая непосредственно и будет заниматьсяя преобразованием. Данный метод нужен
     * для того, чтобы преобразовывать тип Result<T> в тип Result<R>, причем если типом результата
     * будет ошибка, то она преобразуется в ошибку, если Pending, то преобразуется в Pending, если
     * успех то в Success, но там данные типа T, но на выходе у нас контейнер с типом R, поэтому
     * маппер и преобразует тип T в тип R.
     */
    fun<R> mapResult(mapper: ResultMapper<T, R>? = null): Result<R> = when(this) {
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.exception)
        is SuccessResult -> {
            if (mapper == null) throw IllegalStateException("Mapper should not be NULL for success result")
            SuccessResult(mapper(this.data))
        }
    }
}

class PendingResult<T> : Result<T>()

class SuccessResult<T>(
    val data: T
) : Result<T>()

class ErrorResult<T>(
    val exception: Exception
) : Result<T>()

fun<T> Result<T>?.takeSuccess(): T? {
    return if (this is SuccessResult) this.data
    else null
}