package com.mcmouse88.foundation.sideeffect

import android.content.Context

/**
 * Для того, чтобы вызывать side effect из ViewModel нам нужен класс посредник, так как они
 * показываются в активити, а жизненный цикл активити не такой как у ViewModel, и данный посредник
 * будет показывать данные side-effect в том случае если активити активно, а если же нет, то отложит
 * их запуск до лучших времен. При создании плагина нам нужно указать класс этого посредника,
 * (свойство [mediatorClass]), а даальше определить как создается посредник (метод
 * [createMediator()]), и как создается его реализация (метод [createImplementation()]). Метод
 * возвращает null, так как у некоторых посредников может и не быть реализации. В таком случае в
 * посреднике в параметрах указываем [Nothing], указывая, что данного параметра нет. Пример ресурсы
 * и тост, так как их реализация простая, и для них не нужна сама активити, им достаточно контекста
 * самого приложения.
 */
interface SideEffectPlugin<Mediator, Implementation> {

    val mediatorClass: Class<Mediator>

    fun createMediator(applicationContext: Context): SideEffectMediator<Implementation>

    fun createImplementation(mediator: Mediator): Implementation? = null
}