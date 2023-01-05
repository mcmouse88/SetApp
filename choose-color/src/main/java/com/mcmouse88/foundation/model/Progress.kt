package com.mcmouse88.foundation.model

/**
 * sealed class для отображения прогресса выполняемой операции.
 */
sealed class Progress

/**
 * Объект на случай когда в текущий момент никакая операция не выполняется
 */
object EmptyProgress : Progress()

data class PercentageProgress(
    val percentage: Int
) : Progress() {

    companion object {
        val START = PercentageProgress(percentage = 0)
    }
}

// -----Extension-----

fun Progress.isInProgress() = this !is EmptyProgress

fun Progress.getPercentage() = (this as? PercentageProgress)?.percentage ?: PercentageProgress.START.percentage
