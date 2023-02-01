package com.mcmouse88.catadapterespresso.robolectric.apps.activities

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcmouse88.catadapterespresso.R
import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseRobolectricTest
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.containsDrawable
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.require
import com.mcmouse88.catadapterespresso.robolectric.test_utils.extension.requireViewHolderAt
import com.mcmouse88.catadapterespresso.robolectric.test_utils.image_loader.FakeImageLoader
import com.mcmouse88.catadapterespresso.robolectric.test_utils.rules.ImmediateDiffUtilRule
import com.mcmouse88.cats_adapter_espresso.apps.activities.CatDetailsActivity
import com.mcmouse88.cats_adapter_espresso.apps.activities.CatsListActivity
import com.mcmouse88.cats_adapter_espresso.di.RepositoriesModule
import com.mcmouse88.cats_adapter_espresso.model.Cat
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@UninstallModules(RepositoriesModule::class)
class CatsListActivityTest : BaseRobolectricTest() {

    /**
     * Применяем это правило, чтобы убрать многопоточность из внутренних механизмов [RecyclerView]
     */
    @get:Rule
    val immediateDiffUtilRule = ImmediateDiffUtilRule()

    /**
     * [ActivityController] класс из библиотеки [Robolectric], который позволяет запускать активити
     */
    private lateinit var controller: ActivityController<CatsListActivity>

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
        controller = Robolectric.buildActivity(CatsListActivity::class.java)
        controller.setup()
    }

    @After
    fun tearDown() {
        controller.close()
    }

    @Test
    fun catsAndHeadersAreDisplayedInList() {
        val activity = controller.require()
        val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_cats)

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
        }

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

    /**
     * Данный тестовый метод проверяет, что при нажатии на элемент списка запускается вторая
     * активити, т делается это при помощи shadow класса. В shadow классе у [RuntimeEnvironment]
     * есть свойство [nextStartedActivity] и через него можно получить интент с помощью которого
     * была бы запущена вторая активити
     */
    @Test
    fun clickOnCatLaunchesDetails() {
        val activity = controller.require()
        val firstCatHolder = getFirstHolderFromCatsList()
        firstCatHolder.itemView.performClick()

        val expectedIntent = Intent(activity, CatDetailsActivity::class.java)
        val actualIntent = Shadows.shadowOf(RuntimeEnvironment.getApplication())
            .nextStartedActivity

        Assert.assertEquals(expectedIntent.component, actualIntent.component)
        Assert.assertEquals(
            1L,
            actualIntent.getLongExtra(CatDetailsActivity.EXTRA_CAT_ID, -1)
        )
    }

    @Test
    fun clickOnFavoriteTogglesFlag() {
        every { catsRepository.toggleIsFavorite(any()) } answers {
            val cat = firstArg<Cat>()
            catsFlow.value = listOf(
                cat.copy(isFavorite = cat.isFavorite.not()),
                cat2
            )
        }

        getFirstHolderFromCatsList().itemView
            .findViewById<View>(R.id.iv_favorite).performClick()

        assertFavorite(R.drawable.ic_favorite, R.color.highlighted_action)

        getFirstHolderFromCatsList().itemView
            .findViewById<View>(R.id.iv_favorite).performClick()

        assertFavorite(R.drawable.ic_favorite_not, R.color.action)
    }

    /**
     * [Robolectric.flushForegroundThreadScheduler()] необходимо для прокручивания анимации
     * удаления в списке
     */
    @Test
    fun clickOnDeleteRemovesCatFromList() {
        val activity = controller.require()
        val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_cats)
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

    private fun assertFavorite(expectedDrawableRes: Int, expectedTinColorRes: Int? = null) {
        with(getFirstHolderFromCatsList().itemView) {
            val favoriteImageView = findViewById<ImageView>(R.id.iv_favorite)
            Assert.assertTrue(
                favoriteImageView.containsDrawable(expectedDrawableRes, expectedTinColorRes)
            )
        }
    }

    private fun getFirstHolderFromCatsList(): RecyclerView.ViewHolder {
        val recyclerView = controller.require()
            .findViewById<RecyclerView>(R.id.rv_cats)

        return recyclerView.requireViewHolderAt(1)
    }
}