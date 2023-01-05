package com.mcmouse88.box_quiz.contract

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.mcmouse88.box_quiz.Options

typealias ResultListener<T> = (T) -> Unit

/**
 * Extension функция, которая расширяет класс [Fragment], для того, чтобы каждый фрагмент мог
 * получить доступ к интерфейсу [Navigator], и будем считать, что Активити, в которой запускаются
 * фрагменты, и будет являться навигатором
 */
fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showBoxSelectionScreen(options: Options)

    fun showOptionsScreen(options: Options)

    fun showAboutScreen()

    fun showBoxScreen()

    fun goBack()

    fun goToMenu()

    /**
     * Функция для публикации результатов из текущего экрана
     */
    fun<T : Parcelable> publishResult(result: T)

    /**
     * Функция чтобы слушать результаты с других экранов
     */
    fun<T : Parcelable> listenResult(
        clazz: Class<T>,
        owner: LifecycleOwner,
        listener: ResultListener<T>
    )
}