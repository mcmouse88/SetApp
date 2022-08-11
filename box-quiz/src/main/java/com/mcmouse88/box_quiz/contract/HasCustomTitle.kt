package com.mcmouse88.box_quiz.contract

import androidx.annotation.StringRes

/**
 * Если фрагмент реализует данный интерфейс, значит он поддерживает заголовок данного экрана
 */
interface HasCustomTitle {

    @StringRes
    fun getTitleRes(): Int
}