package com.mcmouse88.cats_adapter_espresso.apps.nav_component

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class)
@LargeTest
class NavIntegrationTest : BaseTest() {

    private val cat = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat.jpg",
        description = "Meow-meow",
        isFavorite = false
    )

    private val catsFlow = MutableStateFlow(listOf(cat))

    private lateinit var scenario: ActivityScenario<NavComponentActivity>

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCats() } returns catsFlow
        every { catsRepository.getCatById(any()) } returns catsFlow.map { it.first() }
        every { catsRepository.toggleIsFavorite(any()) } answers {
            catsFlow.value = catsFlow.value.map { it.copy(isFavorite = it.isFavorite.not()) }
        }
        scenario = ActivityScenario.launch(NavComponentActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testFavoriteFlag() {
        clickOnToggleFavoriteInListScreen()
        clickOnCat()
        assertIsFavoriteFlagActiveInDetailsScreen()
        clickOnGoBack()
        assertIsFavoriteFlagActiveInListScreen()
        clickOnCat()
        clickOnToggleFavoriteInDetails()
        clickOnGoBack()
        assertIsFavoriteFlagInactiveInListScreen()
    }

    @Test
    fun testListTitleInActionBar() {
        assertCatsListTitle()
        clickOnCat()
        assertCatDetailsTitle()
        clickOnGoBack()
        assertCatsListTitle()
    }

    @Test
    fun testNavigateUpButton() {
        clickOnCat()
        clickOnNavigateUp()
        assertCatsListTitle()
    }

    @Test
    fun testHardwareBackButton() {
        clickOnCat()
        Espresso.pressBack()
        assertCatsListTitle()
    }

    private fun clickOnToggleFavoriteInListScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ClickOnViewAction.clickOnView(R.id.iv_favorite)))
    }

    private fun clickOnCat() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ViewActions.click()))
    }

    private fun assertIsFavoriteFlagActiveInDetailsScreen() {
        Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.iv_favorite), Matchers.not(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.rv_cats))
        ))).check(ViewAssertions.matches(
            WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)
        ))
    }

    private fun clickOnGoBack() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_back)).perform(ViewActions.click())
    }

    private fun assertIsFavoriteFlagActiveInListScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(1))
            .check(ViewAssertions.matches(AtPosition.atPosition(1, ViewMatchers.hasDescendant(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.iv_favorite),
                    WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action)
                )
            ))))
    }

    private fun clickOnToggleFavoriteInDetails() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.iv_favorite),
            Matchers.not(ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.rv_cats)))
            )).perform(ViewActions.click())
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

    private fun assertCatsListTitle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        scenario.onActivity { activity ->
            Assert.assertEquals(
                context.getString(R.string.fragment_cats_title),
                activity.supportActionBar?.title?.toString()
            )
        }
    }

    private fun assertCatDetailsTitle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        scenario.onActivity { activity ->
            Assert.assertEquals(
                context.getString(R.string.fragment_cat_details),
                activity.supportActionBar?.title?.toString()
            )
        }
    }

    private fun clickOnNavigateUp() {
        Espresso.onView(ViewMatchers.withContentDescription(
            androidx.appcompat.R.string.abc_action_bar_up_description
        )).perform(ViewActions.click())
    }
}