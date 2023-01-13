package com.mcmouse88.okhttp.presentation.main.tabs.settings

import com.mcmouse88.okhttp.domain.Pending
import com.mcmouse88.okhttp.domain.ResultResponse
import com.mcmouse88.okhttp.domain.Success
import com.mcmouse88.okhttp.domain.boxes.BoxesRepository
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.presentation.base.ViewModelExceptionsTest
import com.mcmouse88.okhttp.test_utils.ViewModelTest
import com.mcmouse88.okhttp.test_utils.arranged
import com.mcmouse88.okhttp.test_utils.createBox
import com.mcmouse88.okhttp.test_utils.createBoxAndSettings
import com.mcmouse88.okhttp.utils.requireValue
import io.mockk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest : ViewModelTest() {

    private lateinit var boxesFlow: MutableStateFlow<ResultResponse<List<BoxAndSettings>>>
    private lateinit var boxesRepository: BoxesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        boxesFlow = MutableStateFlow(Pending())
        boxesRepository = createBoxesRepository(boxesFlow)
        viewModel = SettingsViewModel(boxesRepository, accountsRepository, logger)
    }

    @Test
    fun enableBoxEnableBox() {
        val box = createBox()
        viewModel.enableBox(box)
        coVerify(exactly = 1) { boxesRepository.activateBox(box) }
    }

    @Test
    fun disableBoxDisableBox() {
        val box = createBox()
        viewModel.disableBox(box)
        coVerify(exactly = 1) { boxesRepository.deactivateBox(box) }
    }

    @Test
    fun tryAgainReloadsData() {
        arranged()
        viewModel.tryAgain()
        coVerify(exactly = 1) { boxesRepository.reload(BoxesFilter.ALL) }
    }

    @Test
    fun boxSettingsReturnsDataFromRepository() {
        val expectedBoxes1 = Pending<List<BoxAndSettings>>()
        val expectedBoxes2 = listOf(
            createBoxAndSettings(id = 1, name = "Box1", isActive = true),
            createBoxAndSettings(id = 2, name = "Box2", isActive = true)
        )
        val expectedBoxes3 = listOf(
            createBoxAndSettings(id = 2, name = "Box2", isActive = false),
            createBoxAndSettings(id = 3, name = "Box3", isActive = false)
        )

        boxesFlow.value = Pending()
        val result1 = viewModel.boxSetting.requireValue()
        boxesFlow.value = Success(expectedBoxes2)
        val result2 = viewModel.boxSetting.requireValue()
        boxesFlow.value = Success(expectedBoxes3)
        val result3 = viewModel.boxSetting.requireValue()

        Assert.assertEquals(expectedBoxes1, result1)
        Assert.assertEquals(expectedBoxes2, result2.getValueOrNull())
        Assert.assertEquals(expectedBoxes3, result3.getValueOrNull())
    }

    abstract class SettingsViewModelExceptionsTest : ViewModelExceptionsTest<SettingsViewModel>() {

        lateinit var boxesRepository: BoxesRepository
        override lateinit var viewModel: SettingsViewModel

        @Before
        fun setUp() {
            boxesRepository = createBoxesRepository(flowOf())
            viewModel = SettingsViewModel(boxesRepository, accountsRepository, logger)
        }
    }

    class EnableBoxExceptionsTest : SettingsViewModelExceptionsTest() {

        override fun arrangeWithException(e: Exception) {
            coEvery { boxesRepository.activateBox(any()) } throws e
        }

        override fun act() {
            viewModel.enableBox(createBox())
        }
    }

    class DisableBoxExceptionsTest : SettingsViewModelExceptionsTest() {

        override fun arrangeWithException(e: Exception) {
            coEvery { boxesRepository.deactivateBox(any()) } throws e
        }

        override fun act() {
            viewModel.disableBox(createBox())
        }
    }

    class TryAgainExceptionsTest : SettingsViewModelExceptionsTest() {

        override fun arrangeWithException(e: Exception) {
            coEvery { boxesRepository.reload(any()) } throws e
        }

        override fun act() {
            viewModel.tryAgain()
        }
    }

    private companion object {
        fun createBoxesRepository(flow: Flow<ResultResponse<List<BoxAndSettings>>>): BoxesRepository {
            val repository = mockk<BoxesRepository>(relaxed = true)
            every { repository.getBoxesAndSettings(any()) } returns flow
            return repository
        }
    }
}