package com.mcmouse88.cats_adapter_espresso.apps.activities

import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.BaseTest
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.FakeImageLoader
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso.WithDrawable
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Классы с тестами мы дополнительно помечаем аннотациями [RunWith] и указываем внутри класс
 * [AndroidJUnit4], тем самым говорим компилятору, что это инструментальный тест, которые будут
 * выполняться на устройстве, аннотация [HiltAndroidTest], нужна если в проекте используется Hilt,
 * и в таком случае нужна еще одна аннотация [UninstallModules] с помощью которой удаляются крайние
 * зависимости в проекте (глубокие слои не связанные с работой UI), и в скобках прописываются
 * эти модули, если мы удаляем какой-то модуль, то на его место должен встать какой-то фейковый
 * модуль. И последняя аннотация [MediumTest] означает средний по весу тест, та есть
 * инструментальные тесты выполняются дольше и более затратны по ресурсам, делать это необязательно.
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class)
@MediumTest
class CatDetailActivityTest : BaseTest() {

    /**
     * Здесь мы создаем сценарий для каждой активити которая тестируется, и в методах, которые
     * помечены аннотациями [Before] и [After] производим инициализацию фейкового репозитория до
     * того как инициализируется активити, также так как навигация на базе активити, то
     * инициализируем класс Intent для перехвата интентов и создаем сам сценарий, далее
     * инициализируем сценарий, через метод launch, который произведет запуск активити отдельно от
     * самого приложения, и опционально через bundle можно передать параметры. Если мы
     * инициализировали интент и сценарий активити, то в методе [After] необходимо их закрыть.
     */
    private lateinit var scenario: ActivityScenario<CatDetailActivity>

    private val cat = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat.jpg",
        description = "Meow meow",
        isFavorite = true
    )

    private val catFlow = MutableStateFlow(cat)

    @Before
    override fun setUp() {
        every { catsRepository.getCatById(any()) } returns catFlow
        Intents.init()
        scenario = ActivityScenario.launch(
            CatDetailActivity::class.java,
            bundleOf(CatDetailActivity.EXTRA_CAT_ID to 1L)
        )
    }

    @After
    fun tearDown() {
        Intents.release()
        scenario.close()
    }

    /**
     * Принцип работы библиотеки Espresso (и всех остальных UI тестов) в следующем, поиск View
     * через матчер, далее идет действие над view [perform] и проверка результата [check].
     * ```kotlin
     * onView(matcher)
     *     .perform(action)
     *     .check(assertion)
     * ```
     * Иногда [perform] и [check] пишутся отдельно
     */
    @Test
    fun catIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.tv_cat_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("Lucky")))
        Espresso.onView(ViewMatchers.withId(R.id.tv_cat_description))
            .check(ViewAssertions.matches(ViewMatchers.withText("Meow meow")))
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)))
        Espresso.onView(ViewMatchers.withId(R.id.iv_cat))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(FakeImageLoader.createDrawable(cat.photoUrl))))
    }

    /**
     * В блоке arrange инициализируется репозиторий при помощи библиотеки MockK, где мы пишем, что
     * при вызове метода [toggleIsFavorite] репозитория флаг аргумента будет меняться на
     * противоположный.  Далее в этом тесте мы выполняем следующее, ищем кнопку (в данном случае
     * ImageView) по идентификатору, после чего вызываем на нем action click, а далее проверяем,
     * что после нажатия изображение и цвет изменились. Так как библиотека Espresso не обладает
     * методами для определения изображения, то данная функия [withDrawable] была написана
     * самостоятельно.
     */
    @Test
    fun toggleFavoriteTogglesFlag() {
        // arrange
        every { catsRepository.toggleIsFavorite(any()) } answers {
            val cat = firstArg<Cat>()
            val newCat = cat.copy(isFavorite = cat.isFavorite.not())
            catFlow.value = newCat
        }

        // act 1 - turn off favorite flag
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite)).perform(ViewActions.click())
        // assert 1
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite_not, R.color.action)))

        // act 2 - turn on favorite flag
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite)).perform(ViewActions.click())
        // assert 2
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)))
    }

    @Test
    fun clickOnBackFinishesActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_back)).perform(ViewActions.click())
        Assert.assertTrue(scenario.state == Lifecycle.State.DESTROYED)
    }
}