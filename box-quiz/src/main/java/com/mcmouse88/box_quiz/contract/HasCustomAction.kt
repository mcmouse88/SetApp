package com.mcmouse88.box_quiz.contract

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Для того, чтобы можно было кастомизировать toolbar, например добавить на него action, в
 * зависимости от фрагмента в котором он находится создадим еще один интерфейс для этого и класс,
 * чтобы создать action передадим ему объект реализующий интерфейс [Runnable]. Агглтации
 * [DrawableRes] и [StringRes] означают, что данные поля будем получать из соответствующих
 * ресурсов
 */
interface HasCustomAction {
    fun getCustomAction(): CustomAction
}

class CustomAction(
    @DrawableRes val iconRes: Int,
    @StringRes val textRes: Int,
    val onCustomAction: Runnable
)