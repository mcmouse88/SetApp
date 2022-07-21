package com.mcmouse88.mvvm_navigation.navigator

import com.mcmouse88.mvvm_navigation.MainActivity

typealias MainActivityAction = (MainActivity) -> Unit

/**
 * Этот класс позволит запускать действия на определнной Активити(в нашем случае на [MainActivity])
 * тогда и только тогда, когда Активити будет доступна, в случае если Активити недоступно, то
 * действия откладываются, и ждут пока Активити вновь не станет доступно. Чтобы определить
 * сами действия создадим специальный typealias. В самом классе создадим поле, которое в себе
 * будет содержать список действий, и определим поле в котором будет ссылка на Активити, в котором
 * переопределим метод [set], где будем проверять что если Активити не равна null, то все
 * действия, которые есть в списке actions нужно выполнить. Таким образом, все действия, которые
 * будут приходить в список всегда будут выполняться с актуальной активити. После того как действия
 * выполнены мы очищаем список.
 */
class MainActivityActions {

    var mainActivity: MainActivity? = null
    set(activity) {
        field = activity
        if (activity != null) {
            actions.forEach { it(activity) }
            actions.clear()
        }
    }

    private val actions = mutableListOf<MainActivityAction>()

    /**
     * Здесь определим, как мы будем добавлять действия в список, для этого переопределим
     * оператор [invoke], в который будем передавать action, который мы хотим выполнить. Внутри
     * метода проверим, что если текущее Активити равно null, то просто добавим action в список не
     * выполняя его, иначе выполняем действие, не добавляя его в очередь.
     */
    operator fun invoke(action: MainActivityAction) {
        val activity = mainActivity
        if (activity == null) actions += action else action(activity)

    }

    fun clear() {
        actions.clear()
    }
}