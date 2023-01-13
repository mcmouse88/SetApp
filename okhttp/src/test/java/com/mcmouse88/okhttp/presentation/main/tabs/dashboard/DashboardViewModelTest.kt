package com.mcmouse88.okhttp.presentation.main.tabs.dashboard

import android.graphics.Color
import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.boxes.BoxesRepository
import com.mcmouse88.okhttp.domain.boxes.entities.Box
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.createBoxAndSettings
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DashboardViewModelTest : ViewModelTest() {

    @MockK
    lateinit var boxesRepository: BoxesRepository

    private lateinit var boxesFlow: MutableStateFlow<ResultResponse<List<BoxAndSettings>>>
    private lateinit var viewModel: DashboardViewModel

    /**
     * Так как в [DashboardViewModel] есть блок init то поэтому зависмости для нее, а именно flow
     * и boxesRepository нужно инициализировать заранее, что и  происходит в методе, помеченном
     * аннотацией [Before]
     */
    @Before
    fun setUp() {
        boxesFlow = MutableStateFlow(Pending())
        every { boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE) } returns boxesFlow
        viewModel = DashboardViewModel(boxesRepository, accountsRepository, logger)
    }

    @Test
    fun reloadReloadsOnlyActiveData() {
        every { boxesRepository.reload(any()) } just runs
        viewModel.reload()
        verify(exactly = 1) { boxesRepository.reload(BoxesFilter.ONLY_ACTIVE) }
    }

    @Test
    fun boxesReturnsDataFromRepository() {
        val expectedList1 = listOf(
            createBoxAndSettings(id = 2, name = "Red", value = Color.RED),
            createBoxAndSettings(id = 3, name = "Green", value = Color.GREEN),
        )
        val expectedList2 = listOf(
            createBoxAndSettings(id = 3, name = "Green", value = Color.GREEN),
            createBoxAndSettings(id = 4, name = "Blue", value = Color.BLUE),
        )

        boxesFlow.value = Pending()
        val result1 = viewModel.boxes.requireValue()
        boxesFlow.value = Success(expectedList1)
        val result2 = viewModel.boxes.requireValue()
        boxesFlow.value = Success(expectedList2)
        val result3 = viewModel.boxes.requireValue()

        Assert.assertEquals(Pending<List<Box>>(), result1)
        Assert.assertEquals(expectedList1.map { it.box }, result2.getValueOrNull())
        Assert.assertEquals(expectedList2.map { it.box }, result3.getValueOrNull())
        verify(exactly = 1) { boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE) }
    }
}