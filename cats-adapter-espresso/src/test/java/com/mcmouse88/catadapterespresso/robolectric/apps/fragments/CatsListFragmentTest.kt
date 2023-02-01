package com.mcmouse88.catadapterespresso.robolectric.apps.fragments

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseRobolectricTest
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.containsDrawable
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.requireViewHolderAt
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.with
import com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader.FakeImageLoader
import com.mcmouse88.catadapterespresso.robolectric.test_utils.launchHiltFragment
import com.mcmouse88.catadapterespresso.robolectric.test_utils.rules.ImmediateDiffUtilRule
import com.mcmouse88.cats_adapter_espresso.apps.fragments.CatsListFragment
import com.mcmouse88.cats_adapter_espresso.apps.fragments.FragmentRouter
import com.mcmouse88.cats_adapter_espresso.apps.fragments.di.FragmentRouterModule
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.BindValue
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@UninstallModules(RepositoriesModule::class, FragmentRouterModule::class)
class CatsListFragmentTest : BaseRobolectricTest() {

    @get:Rule
    val immediateDiffUtilRule = ImmediateDiffUtilRule()

    /**
     * Вместо того чтобы писать свой модуль для роутера для подмены зависмости, как это было в
     * блоке androidTest, можно пометить переменную аннотацией [BindValue] в совокупности с
     * аннотацией [RelaxedMockK], а также еще указать реальный модуль с роутером в аннотации
     * [UninstallModules] на уровне класса.
     */
    @[BindValue RelaxedMockK]
    lateinit var router: FragmentRouter

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

    private lateinit var scenario: ActivityScenario<*>

    @Before
    override fun setUp() {
        super.setUp()
        every { catsRepository.getCats() } returns catsFlow
        scenario = launchHiltFragment<CatsListFragment>()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun catsAndHeadersAreDisplayedInList() = scenario.with {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_cats)
        val headerHolder = recyclerView.requireViewHolderAt(0)
        val firstCatHolder = recyclerView.requireViewHolderAt(1)
        val secondCatHolder = recyclerView.requireViewHolderAt(2)

        Assert.assertEquals(3, requireNotNull(recyclerView.adapter).itemCount)
        Assert.assertEquals("Cats: 1 … 2", (headerHolder.itemView as TextView).text)

        with(firstCatHolder.itemView) {
            Assert.assertEquals("Lucky", findViewById<TextView>(R.id.tv_cat_name).text)
            Assert.assertEquals("The first cat", findViewById<TextView>(R.id.tv_cat_description).text)
            Assert.assertTrue(
                findViewById<ImageView>(R.id.iv_favorite)
                    .containsDrawable(R.drawable.ic_favorite_not, R.color.action)
            )

            Assert.assertTrue(
                findViewById<ImageView>(R.id.iv_delete)
                    .containsDrawable(R.drawable.ic_delete, R.color.action)
            )

            Assert.assertTrue(
                findViewById<ImageView>(R.id.iv_cat)
                    .containsDrawable(FakeImageLoader.createDrawable("cat1.jpg"))
            )

            with(secondCatHolder.itemView) {
                Assert.assertEquals("Tiger", findViewById<TextView>(R.id.tv_cat_name).text)
                Assert.assertEquals("The second cat", findViewById<TextView>(R.id.tv_cat_description).text)
                Assert.assertTrue(
                    findViewById<ImageView>(R.id.iv_favorite)
                        .containsDrawable(R.drawable.ic_favorite, R.color.highlighted_action)
                )

                Assert.assertTrue(
                    findViewById<ImageView>(R.id.iv_delete)
                        .containsDrawable(R.drawable.ic_delete, R.color.action)
                )

                Assert.assertTrue(
                    findViewById<ImageView>(R.id.iv_cat)
                        .containsDrawable(FakeImageLoader.createDrawable("cat2.jpg"))
                )
            }
        }
    }

    @Test
    fun clickOnCatLaunchesDetails() = scenario.with {
        val firstCatHolder = getFirstHolderFromCatsList()
        firstCatHolder.itemView.performClick()
        verify { router.showDetail(1L) }
    }

    @Test
    fun clickOnFavoriteTogglesFlag() = scenario.with {
        every { catsRepository.toggleIsFavorite(any()) } answers {
            val cat = firstArg<Cat>()
            catsFlow.value = listOf(
                cat.copy(isFavorite = cat.isFavorite.not())
            )
        }
        getFirstHolderFromCatsList().itemView
            .findViewById<View>(R.id.iv_favorite).performClick()

        assertFavorite(R.drawable.ic_favorite, R.color.highlighted_action)

        getFirstHolderFromCatsList().itemView
            .findViewById<View>(R.id.iv_favorite).performClick()

        assertFavorite(R.drawable.ic_favorite_not, R.color.action)
    }

    @Test
    fun clickOnDeleteRemovesCatFromList() = scenario.with {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_cats)
        every { catsRepository.delete(any()) } answers {
            catsFlow.value = listOf(cat2)
        }

        getFirstHolderFromCatsList().itemView
            .findViewById<View>(R.id.iv_delete).performClick()
        Robolectric.flushForegroundThreadScheduler()

        recyclerView.scrollToPosition(0)
        val headerHolder = requireNotNull(recyclerView.findViewHolderForAdapterPosition(0))
        recyclerView.scrollToPosition(1)
        val secondCatHolder = requireNotNull(recyclerView.findViewHolderForAdapterPosition(1))

        Assert.assertEquals(2, requireNotNull(recyclerView.adapter).itemCount)
        Assert.assertEquals("Cats: 1 … 1", (headerHolder.itemView as TextView).text)

        with(secondCatHolder.itemView) {
            Assert.assertEquals("Tiger", findViewById<TextView>(R.id.tv_cat_name).text)
            Assert.assertEquals("The second cat", findViewById<TextView>(R.id.tv_cat_description).text)
            Assert.assertTrue(
                findViewById<ImageView>(R.id.iv_favorite)
                    .containsDrawable(R.drawable.ic_favorite, R.color.highlighted_action)
            )
            Assert.assertTrue(
                findViewById<ImageView>(R.id.iv_delete)
                    .containsDrawable(R.drawable.ic_delete, R.color.action)
            )

            Assert.assertTrue(
                findViewById<ImageView>(R.id.iv_cat)
                    .containsDrawable(FakeImageLoader.createDrawable("cat2.jpg"))
            )
        }
    }

    private fun Activity.assertFavorite(expectedDrawableRes: Int, expectedTintColorRes: Int? = null) {
        with(getFirstHolderFromCatsList().itemView) {
            val favoriteImageView = findViewById<ImageView>(R.id.iv_favorite)

            Assert.assertTrue(
                favoriteImageView.containsDrawable(expectedDrawableRes, expectedTintColorRes)
            )
        }
    }

    private fun Activity.getFirstHolderFromCatsList(): RecyclerView.ViewHolder {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_cats)
        return recyclerView.requireViewHolderAt(1)
    }
}