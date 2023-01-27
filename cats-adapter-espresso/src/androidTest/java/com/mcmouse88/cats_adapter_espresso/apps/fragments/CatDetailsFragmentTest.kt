package com.mcmouse88.cats_adapter_espresso.apps.fragments

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.cats_adapter_espresso.apps.fragments.di.FragmentRouterModule
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.BaseTest
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.FakeImageLoader
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso.WithDrawable
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.launchHiltFragment
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

/**
 * При навигации на базе фрагментов в приложении для организации навигации используется
 * интерфейс [FragmentRouter], который мы инжектить в тестируемый класс, однако, вместо реального
 * роутера будем использовать фейковый роутер так как тестируем отдельно каждый фрагмент, поэтому
 * в аннотации [UninstallModules] в параметрах укажем это.
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class, FragmentRouterModule::class)
@MediumTest
class CatDetailsFragmentTest : BaseTest() {

    @Inject
    lateinit var fragmentRouter: FragmentRouter

    private val cat = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat.jpg",
        description = "Meow-meow",
        isFavorite = true
    )

    private val catFlow = MutableStateFlow(cat)

    private lateinit var scenario: AutoCloseable

    /**
     * Так как в проекте присутствует Hilt, то стандартный метод [launchFragmentInContainer]
     * использовать не получится, так как фрагмент не получит зависимости и упадет с ошибкой, в
     * связи с чем для тестирования нами был написан метод [launchHiltFragment]
     */
    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCatById(any()) } returns catFlow
        scenario = launchHiltFragment {
            CatDetailsFragment.newInstance(cat.id)
        }
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun catIsDisplayed() {
        // assert
        Espresso.onView(ViewMatchers.withId(R.id.tv_cat_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("Lucky")))
        Espresso.onView(ViewMatchers.withId(R.id.tv_cat_description))
            .check(ViewAssertions.matches(ViewMatchers.withText("Meow-meow")))
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)))
        Espresso.onView(ViewMatchers.withId(R.id.iv_cat))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(FakeImageLoader.createDrawable(cat.photoUrl))))
    }

    @Test
    fun toggleFavoriteToggleFlag() {
        every { catsRepository.toggleIsFavorite(any()) } answers {
            val cat = firstArg<Cat>()
            val newCat = cat.copy(isFavorite = cat.isFavorite.not())
            catFlow.value = newCat
        }

        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite_not, R.color.action)))

        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.iv_favorite))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)))
    }

    /**
     * На данном тесте проверяем, что если была нажата кнопка с определенным идентификатором,
     * то через метод [verify] определяем, что у объекта [FragmentRouter] вызывается метод
     * [goBack]
     */
    @Test
    fun clickOnBackFinishesActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_back)).perform(ViewActions.click())

        verify { fragmentRouter.goBack() }
    }

    /**
     * Фейковый модуль, который будет использоваться вместо реального, так как модуль локальный,
     * то он будет работать только в пределах данного тестового класса, а не во всем тестовом
     * модуле. По этой причине в классе [CatsListFragmentTest] мы также создаем такой же модуль.
     * Также фейковые модули нужно помечать аннотациями [Singleton] иначе тесты могут получить
     * разные объекты из-за чего будут завершаться с ошибкой.
     */
    @[Module InstallIn(SingletonComponent::class)]
    class FakeFragmentRouterModule {

        @[Provides Singleton]
        fun bindRouter(): FragmentRouter {
            return mockk(relaxed = true)
        }
    }
}