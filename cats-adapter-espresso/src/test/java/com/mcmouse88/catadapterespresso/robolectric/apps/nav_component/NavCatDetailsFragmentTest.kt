package com.mcmouse88.catadapterespresso.robolectric.apps.nav_component

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseRobolectricTest
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.containsDrawable
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.with
import com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader.FakeImageLoader
import com.mcmouse88.catadapterespresso.robolectric.test_utils.launchNavHiltFragment
import com.mcmouse88.cats_adapter_espresso.apps.nav_component.NavCatsDetailFragment
import com.mcmouse88.cats_adapter_espresso.apps.nav_component.NavCatsDetailFragmentArgs
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@UninstallModules(RepositoriesModule::class)
class NavCatDetailsFragmentTest : BaseRobolectricTest() {

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

    private lateinit var scenario: ActivityScenario<*>

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCatById(any()) } returns catFlow
        val args = NavCatsDetailFragmentArgs(catId = 1L)
        scenario = launchNavHiltFragment<NavCatsDetailFragment>(navController, args.toBundle())
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun catIsDisplayed() = scenario.with {
        Assert.assertEquals(
            "Lucky",
            findViewById<TextView>(R.id.tv_cat_name).text
        )

        Assert.assertEquals(
            "Meow-meow",
            findViewById<TextView>(R.id.tv_cat_description).text
        )

        Assert.assertTrue(
            findViewById<ImageView>(R.id.iv_favorite)
                .containsDrawable(R.drawable.ic_favorite, R.color.highlighted_action)
        )

        Assert.assertTrue(
            findViewById<ImageView>(R.id.iv_cat)
                .containsDrawable(FakeImageLoader.createDrawable(cat.photoUrl))
        )
    }

    @Test
    fun toggleFavoriteTogglesFlag() = scenario.with {
        every { catsRepository.toggleIsFavorite(any()) } answers {
            val cat = firstArg<Cat>()
            val newCat = cat.copy(isFavorite = cat.isFavorite.not())
            catFlow.value  = newCat
        }
        val favoriteImageView = findViewById<ImageView>(R.id.iv_favorite)

        favoriteImageView.performClick()
        Assert.assertTrue(
            favoriteImageView.containsDrawable(
                R.drawable.ic_favorite_not,
                R.color.action
            )
        )

        favoriteImageView.performClick()
        Assert.assertTrue(
            favoriteImageView.containsDrawable(
                R.drawable.ic_favorite,
                R.color.highlighted_action
            )
        )
    }

    @Test
    fun clickOnBackFinishesActivity() = scenario.with {
        findViewById<View>(R.id.btn_go_back).performClick()
        verify { navController.navigateUp() }
    }
}