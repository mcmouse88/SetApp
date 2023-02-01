package com.mcmouse88.catadapterespresso.robolectric.viewmodel

import com.mcmouse88.catadapterespresso.robolectric.test_utils.base.BaseTest
import com.mcmouse88.cats_adapter_espresso.model.Cat
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import com.mcmouse88.cats_adapter_espresso.viewmodel.CatDetailsViewModel
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CatDetailsViewModelTest : BaseTest() {

    @RelaxedMockK
    lateinit var catsRepository: CatsRepository

    private lateinit var viewModel: CatDetailsViewModel

    private val catId = 1L

    private val cat = Cat(
        id = 1,
        name = "Cat",
        photoUrl = "url",
        description = "desc",
        isFavorite = false
    )

    private val updatedCat = cat.copy(name = "New name")

    private val flow = MutableStateFlow(cat)

    @Before
    fun setUp() {
        every { catsRepository.getCatById(catId) } returns flow
        viewModel = CatDetailsViewModel(catsRepository, catId)
    }

    @Test
    fun toggleFavorite_callsToggleFavorite() {
        viewModel.toggleFavorite()
        verify { catsRepository.toggleIsFavorite(cat) }
    }

    @Test
    fun init_listensForCat() {
        val cat1 = viewModel.catLiveData.value
        flow.value = updatedCat
        val cat2 = viewModel.catLiveData.value

        Assert.assertEquals(cat, cat1)
        Assert.assertEquals(updatedCat, cat2)
    }
}