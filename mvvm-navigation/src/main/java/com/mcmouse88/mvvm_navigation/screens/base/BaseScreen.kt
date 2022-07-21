package com.mcmouse88.mvvm_navigation.screens.base

import java.io.Serializable

/**
 * BaseScreen нужен для того, чтобы [ViewModel] не содержала android зависимостей (не должно быть
 * ссылок ни на Активити, ни на фрагменты и т.п.), поэтому управлять навигацией будем при помощи
 * абстрактной сущности
 */
interface BaseScreen : Serializable {
}