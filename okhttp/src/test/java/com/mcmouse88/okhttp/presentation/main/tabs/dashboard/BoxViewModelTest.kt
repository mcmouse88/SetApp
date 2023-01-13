package com.mcmouse88.okhttp.presentation.main.tabs.dashboard

import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.boxes.BoxesRepository
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.createBoxAndSettings
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BoxViewModelTest : ViewModelTest() {

    lateinit var flow: MutableStateFlow<ResultResponse<List<BoxAndSettings>>>

    @MockK
    lateinit var boxesRepository: BoxesRepository

    lateinit var viewModel: BoxViewModel

    private val boxId = 1L
    private val anotherBoxId = 2L

    @Before
    fun setUp() {
        flow = MutableStateFlow(Pending())
        every { boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE) } returns flow
        viewModel = BoxViewModel(boxId, boxesRepository, accountsRepository, logger)
    }

    @Test
    fun shouldExitEventIsFiredAfterDisablingBox() {
        val listWithBox = listOf(
            createBoxAndSettings(id = boxId)
        )
        
        val listWithoutBox = listOf(
            createBoxAndSettings(id = anotherBoxId)
        )

        flow.value = Success(listWithBox)
        val shouldExitEvent1 = viewModel.shouldExitEvent.requireValue().get()!!
        flow.value = Success(listWithoutBox)
        val shouldExitEvent2 = viewModel.shouldExitEvent.requireValue().get()!!
        Assert.assertFalse(shouldExitEvent1)
        Assert.assertTrue(shouldExitEvent2)
    }
}