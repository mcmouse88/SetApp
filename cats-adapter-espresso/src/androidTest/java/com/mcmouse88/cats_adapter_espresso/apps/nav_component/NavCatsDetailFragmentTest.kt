package com.mcmouse88.cats_adapter_espresso.apps.nav_component

import androidx.navigation.NavController
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.BaseTest
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.FakeImageLoader
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.espresso.WithDrawable
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.launchNavHiltFragment
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class)
@MediumTest
class NavCatsDetailFragmentTest : BaseTest() {

    @RelaxedMockK
    lateinit var navController: NavController

    private val cat = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat.jpg",
        description = "Meow-meow",
        isFavorite = true
    )

    private val catFlow = MutableStateFlow(cat)

    private lateinit var scenario: AutoCloseable

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCatById(any()) } returns catFlow
        val args = NavCatsDetailFragmentArgs(1)
        scenario = launchNavHiltFragment<NavCatsDetailFragment>(navController, args.toBundle())
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun catIsDisplayed() {
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
    fun toggleFavoriteTogglesFlag() {
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

    @Test
    fun clickOnBackFinishesActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_go_back)).perform(ViewActions.click())

        verify { navController.popBackStack() }
    }
}