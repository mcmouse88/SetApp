package com.mcmouse88.cats_adapter_espresso.apps.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.BaseTest
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso.*
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Данный тестовый класс тестирует как взаимодействуют активити между собой, то есть запускается
 * первая активити, производятся какие-то действия, осуществляется переход на второе активити,
 * там производятся какие-то действия, и осуществляется переход обратно на первое активити,
 * такие тесты уже помечаются аннотацией [LargeTest]. Такие тесты уже называются интеграционными.
 * Конкретно в этом тесте на первом активите ставим лайк на элементе списка, осуществляем переход
 * на второе активити по клику на элемент списка, проверяем что на втором активити с детальной
 * информацией также отображается, что итем лайкнут, возвращаемся на первое активити, проверяем, что
 * на элементе лайк сохранился, опять по клику переходим на второе активити, убираем там лайк,
 * возвращаемся на первое активити, и проверяем, что лайк на итеме также убрался.
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class)
@LargeTest
class CatsActivitiesIntegrationTest : BaseTest() {

    private lateinit var scenario: ActivityScenario<CatsListActivity>

    private val cat = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat1.jpg",
        description = "The first cat",
        isFavorite = false
    )

    private val catsFlow = MutableStateFlow(listOf(cat))

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCats() } returns catsFlow
        every { catsRepository.getCatById(any()) } returns catsFlow.map { it.first() }
        every { catsRepository.toggleIsFavorite(any()) } answers {
            catsFlow.value = catsFlow.value.map { it.copy(isFavorite = it.isFavorite.not()) }
        }

        Intents.init()
        scenario = ActivityScenario.launch(CatsListActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        scenario.close()
    }

    @Test
    fun testFavoriteFlag() {
        clickOnToggleFavoriteInListScreen()
        clickOnCat()
        assertIsFavoriteFlagActiveInDetailScreen()
        clickOnGoBack()
        assertIsFavoriteFlagActiveInListScreen()
        clickOnCat()
        clickOnToggleFavoriteInDetails()
        clickOnGoBack()
        assertIsFavoriteFlagInactiveInListScreen()
    }

    private fun clickOnToggleFavoriteInListScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ClickOnViewAction.clickOnView(R.id.iv_favorite)))
    }

    private fun clickOnCat() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ViewActions.click()))
    }

    private fun assertIsFavoriteFlagActiveInDetailScreen() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.iv_favorite),
            Matchers.not(ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.rv_cats)))))
            .check(ViewAssertions.matches(WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)))
    }

    private fun clickOnGoBack() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_back)).perform(ViewActions.click())
    }

    private fun assertIsFavoriteFlagActiveInListScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(1))
            .check(
                ViewAssertions.matches(
                    AtPosition.atPosition(
                        1, ViewMatchers.hasDescendant(
                            Matchers.allOf(
                                ViewMatchers.withId(R.id.iv_favorite),
                                WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)
                            )
                        )
                    )
                )
            )
    }

    private fun clickOnToggleFavoriteInDetails() {
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.iv_favorite),
                Matchers.not(ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.rv_cats)))
            )
        ).perform(ViewActions.click())
    }

    private fun assertIsFavoriteFlagInactiveInListScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(1))
            .check(ViewAssertions.matches(AtPosition.atPosition(1, ViewMatchers.hasDescendant(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.iv_favorite),
                    WithDrawable.withDrawable(R.drawable.ic_favorite_not, R.color.action)
                )
            ))))
    }
}