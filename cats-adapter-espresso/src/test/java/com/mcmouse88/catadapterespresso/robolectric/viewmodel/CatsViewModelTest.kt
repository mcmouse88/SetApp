package com.mcmouse88.catadapterespresso.robolectric.viewmodel

import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseTest
import com.mcmouse88.cats_adapter_espresso.model.Cat
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatListItem
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatsViewModel
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CatsViewModelTest : BaseTest() {

    @RelaxedMockK
    lateinit var catsRepository: CatsRepository

    private lateinit var viewModel: CatsViewModel

    private val catsFlow = MutableStateFlow<List<Cat>>(emptyList())

    private val cat = Cat(
        id = 1,
        name = "Cat",
        photoUrl = "url",
        description = "desc",
        isFavorite = false
    )

    private val anotherCat = Cat(
        id = 1,
        name = "Cat",
        photoUrl = "url",
        description = "desc",
        isFavorite = false
    )

    @Before
    fun setup() {
        every { catsRepository.getCats() } returns catsFlow
        viewModel = CatsViewModel(catsRepository)
    }

    @Test
    fun deleteCat_callsDelete() {
        val catListItem = CatListItem.CatItem(cat)
        viewModel.deleteCat(catListItem)

        verify { catsRepository.delete(cat) }
    }

    @Test
    fun toggleFavorite_callsToggleFavorite() {
        val catListItem = CatListItem.CatItem(cat)
        viewModel.toggleCat(catListItem)
        verify { catsRepository.toggleIsFavorite(cat) }
    }

    @Test
    fun init_collectsCatList() {
        catsFlow.value = listOf(cat, anotherCat)
        val listItem = viewModel.catsLiveData.value

        Assert.assertEquals(
            listOf(
                CatListItem.Header(0, 1, 2),
                CatListItem.CatItem(cat),
                CatListItem.CatItem(anotherCat)
            ),
            listItem
        )
    }

    @Test
    fun init_placesHeadersCorrectly() {
        catsFlow.value = List(27) { cat }
        val listItem = requireNotNull(viewModel.catsLiveData.value)

        Assert.assertEquals(1, (listItem[0] as CatListItem.Header).fromIndex)
        Assert.assertEquals(10, (listItem[0] as CatListItem.Header).toIndex)

        Assert.assertEquals(11, (listItem[11] as CatListItem.Header).fromIndex)
        Assert.assertEquals(20, (listItem[11] as CatListItem.Header).toIndex)

        Assert.assertEquals(21, (listItem[22] as CatListItem.Header).fromIndex)
        Assert.assertEquals(27, (listItem[22] as CatListItem.Header).toIndex)
    }
}