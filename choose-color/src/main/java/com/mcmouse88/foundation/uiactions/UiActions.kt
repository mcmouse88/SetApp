package com.mcmouse88.foundation.uiactions

/**
 * Позволяет выполнять действия с самых различных экранов, например показывать toast, получать
 * доступ к ресурсам
 */
interface UiActions {

    fun showToast(message: String)

    /**
     * vararg для того, что если у нас строка с какими-либо параметрами, то их можно было бы
     * перечислить
     */
    fun getString(messageRes: Int, vararg args: Any): String
}