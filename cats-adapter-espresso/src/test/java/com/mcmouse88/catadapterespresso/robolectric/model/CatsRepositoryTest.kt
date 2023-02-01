package com.mcmouse88.catadapterespresso.robolectric.model

import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseTest
import com.mcmouse88.catadapterespresso.robolectric.test_utils.runFlowTest
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatsRepositoryTest : BaseTest() {

    private lateinit var repository: CatsRepository

    @Before
    fun setUp() {
        repository = CatsRepository()
    }

    @Test
    fun getCatById_emitsCats() = runTest {
        val expectedCat = repository.getCats().first().random()
        val actualCat = repository.getCatById(expectedCat.id).first()
        Assert.assertEquals(expectedCat, actualCat)
    }

    @Test
    fun getCatById_withInvalidId_emitsAll() = runTest {
        val cat = repository.getCatById(99_999).firstOrNull()
        Assert.assertNull(cat)
    }

    @Test
    fun getCatById_afterDelete_emitsNull() = runFlowTest {
        val cat = repository.getCats().first().random()
        val catFlow = repository.getCatById(cat.id)

        val collectedCat = catFlow.startCollecting()
        repository.delete(cat)

        Assert.assertEquals(
            listOf(cat, null),
            collectedCat
        )
    }

    @Test
    fun getCatById_afterToggleFavorite_emitsUpdatedCat() = runFlowTest {
        val cat = repository.getCats().first().random()
        val catFlow = repository.getCatById(cat.id)

        val collectedCat = catFlow.startCollecting()
        repository.toggleIsFavorite(cat)

        val expectedUpdateCat = cat.copy(isFavorite = cat.isFavorite.not())
        Assert.assertEquals(
            listOf(cat, expectedUpdateCat),
            collectedCat
        )
    }

    @Test
    fun getCats_afterDelete_emitsUpdatedList() = runFlowTest {
        val cat = repository.getCats().first().random()
        val catsFlow = repository.getCats()

        val collectedCat = catsFlow.startCollecting()
        repository.delete(cat)

        Assert.assertEquals(2, collectedCat.size)
        Assert.assertTrue(collectedCat[0].contains(cat))
        Assert.assertFalse(collectedCat[1].contains(cat))
    }

    @Test
    fun getCats_afterToggleFavorite_emitsUpdatedList() = runFlowTest {
        val cat = repository.getCats().first().random()
        val catsFlow = repository.getCats()

        val collectedCats = catsFlow.startCollecting()
        repository.toggleIsFavorite(cat)

        Assert.assertEquals(2, collectedCats.size)
        Assert.assertEquals(cat.isFavorite, collectedCats[0].first { it.id == cat.id }.isFavorite)
        Assert.assertEquals(cat.isFavorite.not(), collectedCats[1].first { it.id == cat.id }.isFavorite)
    }
}