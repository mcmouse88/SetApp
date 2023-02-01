package com.mcmouse88.catadapterespresso.robolectric.apps.activities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseRobolectricTest
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.containsDrawable
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.with
import com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader.FakeImageLoader
import com.mcmouse88.cats_adapter_espresso.apps.activities.CatDetailsActivity
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Отличие полноценных UI тестов от [Robolectric] в том, что в [Robolectric] нельзя использовать
 * интеграционные тесты(когда с одной активити(фрагмента) осуществляется переход на другое, и
 * тестируется их взаимодействие), а также нельзя попиксельно сравнить используемые картинки. Здесь
 * для разнообразия используется уже не контроллер, а [ActivityScenario], чтобы показать, что
 * [Robolectric] совместим и с другими тестируемыми компонентами (в данном случа с библиотекой
 * androidx.test)
 */
@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@UninstallModules(RepositoriesModule::class)
class CatDetailsActivityTest : BaseRobolectricTest() {

    private lateinit var scenario: ActivityScenario<CatDetailsActivity>

    private val cat = Cat(
        id = 1,
        name = "Lucky",
        photoUrl = "cat.jpg",
        description = "Meow-meow",
        isFavorite = true
    )

    private val catFlow = MutableStateFlow(cat)

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCatById(any()) } returns catFlow
        scenario = ActivityScenario.launch(
            CatDetailsActivity::class.java,
            bundleOf(
                CatDetailsActivity.EXTRA_CAT_ID to 1L
            )
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    /**
     * В данном тесте имеется написанный нами метод [with], внутри которого имеется доступ
     * ко всем методам активити(см. реализацию).
     */
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
            catFlow.value = newCat
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
                R.drawable.ic_favorite, R.color.highlighted_action
            )
        )
    }

    @Test
    fun clickOnBackFinishesActivity() = scenario.with {
        findViewById<View>(R.id.btn_go_back).performClick()
        Assert.assertTrue(isFinishing)
    }
}