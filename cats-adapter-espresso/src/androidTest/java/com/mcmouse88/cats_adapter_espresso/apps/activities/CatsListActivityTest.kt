package com.mcmouse88.cats_adapter_espresso.apps.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.BaseTest
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.FakeImageLoader
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso.*
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class)
@MediumTest
class CatsListActivityTest : BaseTest() {

    private lateinit var scenario: ActivityScenario<CatsListActivity>

    private val cat1 = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat1.jpg",
        description = "The first cat",
        isFavorite = false
    )

    private val cat2 = Cat(
        id = 2,
        name = "Tiger",
        photoUrl = "cat2.jpg",
        description = "The second cat",
        isFavorite = true
    )

    private val catsFlow = MutableStateFlow(listOf(cat1, cat2))

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCats() } returns catsFlow
        Intents.init()
        scenario = ActivityScenario.launch(CatsListActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        scenario.close()
    }

    @Test
    fun catsAndHeadersAreDisplayedInList() {
        // act
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(0))
            .check(ViewAssertions.matches(AtPosition.atPosition(0, ViewMatchers.withText("Cats: 1 … 2"))))

        // assert
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(1))
            .check(ViewAssertions.matches(AtPosition.atPosition(1, Matchers.allOf(
               ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.tv_cat_name), ViewMatchers.withText("Lucky"))),
               ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.tv_cat_description), ViewMatchers.withText("The first cat"))),
               ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_favorite), WithDrawable.withDrawable(R.drawable.ic_favorite_not, R.color.action))),
               ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_delete), WithDrawable.withDrawable(R.drawable.ic_delete, R.color.action))),
               ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_cat), WithDrawable.withDrawable(FakeImageLoader.createDrawable("cat1.jpg"))))
            ))))

        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(2))
            .check(ViewAssertions.matches(AtPosition.atPosition(2, Matchers.allOf(
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.tv_cat_name), ViewMatchers.withText("Tiger"))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.tv_cat_description), ViewMatchers.withText("The second cat"))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_favorite), WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_delete), WithDrawable.withDrawable(R.drawable.ic_delete, R.color.action))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_cat), WithDrawable.withDrawable(FakeImageLoader.createDrawable("cat2.jpg"))))
            ))))

        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .check(ViewAssertions.matches(WithItemsCount.withItemsCount(3))) // 1 header + 2 cats
    }

    @Test
    fun clickOnCatLaunchesDetails() {
        // arrange
        every { catsRepository.getCatById(any()) } returns flowOf(cat1)

        // act
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ViewActions.click()))

        // assert
        Intents.intended(IntentMatchers.hasExtra(CatDetailActivity.EXTRA_CAT_ID, 1L))
    }

    @Test
    fun clickOnFavoriteToggleFlag() {
        // arrange
        every { catsRepository.toggleIsFavorite(any()) } answers {
            val cat = firstArg<Cat>()
            catsFlow.value = listOf(
                cat.copy(isFavorite = cat.isFavorite.not()),
                cat2
            )
        }

        // act 1 - turn on a favorite flag
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ClickOnViewAction.clickOnView(R.id.iv_favorite)))

        // assert 1
        assertFavorite(R.drawable.ic_favorite, R.color.highlighted_action)

        // act 2 - turn off a favorite toggle
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ClickOnViewAction.clickOnView(R.id.iv_favorite)))

        // assert 2
        assertFavorite(R.drawable.ic_favorite_not, R.color.action)
    }

    @Test
    fun clickOnDeleteRemovesCatFromList() {
        // arrange
        every { catsRepository.delete(any()) } answers {
            catsFlow.value = listOf(cat2)
        }

        // act
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.actionOnItemAtPosition(1, ClickOnViewAction.clickOnView(R.id.iv_delete)))

        // assert
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(0))
            .check(ViewAssertions.matches(AtPosition.atPosition(0, ViewMatchers.withText("Cats: 1 … 1"))))
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(1))
            .check(ViewAssertions.matches(AtPosition.atPosition(1, Matchers.allOf(
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.tv_cat_name), ViewMatchers.withText("Tiger"))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.tv_cat_description), ViewMatchers.withText("The second cat"))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_favorite), WithDrawable.withDrawable(R.drawable.ic_favorite, R.color.highlighted_action))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_delete), WithDrawable.withDrawable(R.drawable.ic_delete, R.color.action))),
                ViewMatchers.hasDescendant(Matchers.allOf(ViewMatchers.withId(R.id.iv_cat), WithDrawable.withDrawable(FakeImageLoader.createDrawable(cat2.photoUrl))))
            ))))
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .check(ViewAssertions.matches(WithItemsCount.withItemsCount(2))) // 1 header + 1 cat
    }

    private fun assertFavorite(expectedDrawableRes: Int, expectedTintColorRes: Int? = null) {
        Espresso.onView(ViewMatchers.withId(R.id.rv_cats))
            .perform(AtPosition.scrollToPosition(1))
            .check(ViewAssertions.matches(AtPosition.atPosition(1,
            ViewMatchers.hasDescendant(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.iv_favorite),
                    WithDrawable.withDrawable(expectedDrawableRes, expectedTintColorRes)
                )
            ))))
    }
}