package com.mcmouse88.foundation

import android.app.Application
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.mcmouse88.choose_color.App
import com.mcmouse88.choose_color.MainActivity
import com.mcmouse88.choose_color.R
import com.mcmouse88.foundation.utils.Event
import com.mcmouse88.foundation.utils.ResourceActions
import com.mcmouse88.foundation.navigator.Navigator
import com.mcmouse88.foundation.uiactions.UiActions
import com.mcmouse88.foundation.views.BaseScreen
import com.mcmouse88.foundation.views.LiveEvent
import com.mcmouse88.foundation.views.MutableLiveEvent

const val ARG_SCREEN = "arg_screen"


/**
 * Не является обычной [ViewModel], так как она зависит от классов android sdk, и наследуется от
 * [AndroidViewModel], мы ее используем не как обычную [ViewModel], а как место для реализации
 * [Navigator] и [UiActions]. Здесь мы не можем безопасно выполнять навигацию, так как нам
 * нужно быть уверенным, что на момент выполнения этих действий Активити была активна. Поэтому
 * для навигации мы используем вспомогательный класс [ResourceActions] (свойство
 * [whenActivityActive]), который представляет собой очередь действий, которые запускаются только
 * тогда, когда Активити активно, то есть код который будет выполняться в фигурных скобках
 * методов [launch()] и [goBack()] будет выполняться только в том случае, если Активити
 * активно.
 */
class ActivityScopeViewModel(
    application: Application
) : AndroidViewModel(application), Navigator, UiActions {

    val whenActivityActive = ResourceActions<MainActivity>()

    /**
     * Сам результат мы передаем при помощи [LiveData], внутри который класс [Event] с типом Any.
     * У нас это записано как [LiveEvent<Any>], но на самом деле это typealias, созданный в класса
     * [BaseViewModel], который выглядит вот так:
     * ```css
     * typealias LiveEvent<T> = LiveData<Event<T>>
     * typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>
     * ```
     * [Event] - это однократное событие, которое выполняется один раз на протяжении жизненного
     * цикла [ViewModel]
     */
    private val _result = MutableLiveEvent<Any>()
    val result: LiveEvent<Any>
        get() = _result

    override fun launch(screen: BaseScreen) = whenActivityActive {
        launchFragment(it, screen)
    }

    /**
     * Логика передачи результата реализована в методе [goBack()]. Опционально какая-либо ViewModel
     * может опционально передать результат. например у нас это происходит на экране
     * [ChangeColorFragment] (а если точнее то в [ChangeColorViewModel]), при нажатии на кнопку
     * save мы передаем в качестве результата цвет выбранный пользователем. Таким образом
     * при вызове метода [goBack()] мы проверяем аргумент результата на null, и если он не null,
     * то присваиваем его значение в свойство [result]. Далее когда предыдущий фрагмент становится
     * доступным, и со стороны [MainActivity] вызывается метод onFragmentViewCreated, внутри
     * которого вызывается метод [notifyScreenUpdates()], где мы и получаем результат.
     */
    override fun goBack(result: Any?) = whenActivityActive {
        if (result != null) {
            _result.value = Event(result)
        }
        it.onBackPressed()
    }

    override fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Символ * указывает компилятору на то, что мы ему передаем не массив элементов переданных
     * как varargs, а именно как vararg, то есть каждый элемент по отдельности
     */
    override fun getString(messageRes: Int, vararg args: Any): String {
        return getApplication<App>().getString(messageRes, *args)
    }

    /**
     * Метод [launchFragment()] принимает три параметра, это само Активити, экран, который нужно
     * запустить (Все наши фрагменты содержат в себе объект реализующий интерфейс [BaseScreen]), а
     * также значение типа Boolean, в котором указывается нужно ли добавлять экран в backStack
     * (данный аргумент равен false только в одном случает, при первом старте приложения).
     * В самом методе сначала мы получаем фрагмент, который нужно запустить из объекта типа
     * [BaseScreen], который реализует интерфейс [Serializable], и его можно передать в качестве
     * аргумента, что позволяет нам избавится от создания методов [newInstance()] в самих
     * фрагментах. Строчка [screen.javaClass.enclosingClass.newInstance()] позволяет вернуть
     * экземпляр самого фрагмента (нужно только прописать явный каст) то есть данная строчка
     * позволяет вернуть объект класса внутри которого находится объект класса, у которого данные
     * методы вызваны. Можно также сами классы Screen вынести во [ViewModel], или сделать
     * вообще отдельным независимым классом, но тогда придется прописывать логику получения
     * фрагмента из его объекта.
     */
    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack: Boolean = true) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment

        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.setCustomAnimations(
            R.anim.enter,
            R.anim.exit,
            R.anim.pop_enter,
            R.anim.pop_exit
        )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCleared() {
        whenActivityActive.clear()
        super.onCleared()
    }
}