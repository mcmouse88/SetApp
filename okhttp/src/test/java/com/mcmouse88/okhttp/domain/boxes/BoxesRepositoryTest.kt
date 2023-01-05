package com.mcmouse88.okhttp.domain.boxes

import com.mcmouse88.okhttp.domain.*
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import com.mcmouse88.okhttp.domain.boxes.entities.BoxesFilter
import com.mcmouse88.okhttp.test_utils.*
import com.mcmouse88.okhttp.utils.async.LazyFlowFactory
import com.mcmouse88.okhttp.utils.async.LazyFlowSubject
import com.mcmouse88.okhttp.utils.async.SuspendValueLoader
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoxesRepositoryTest {

    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var accountsRepository: AccountsRepository

    @MockK
    lateinit var boxesSource: BoxesSource

    @MockK
    lateinit var lazyFlowFactory: LazyFlowFactory

    @MockK
    lateinit var lazyFlowSubject: LazyFlowSubject<BoxesFilter, List<BoxAndSettings>>

    lateinit var boxesRepository: BoxesRepository

    @Before
    fun setUp() {
        every {
            lazyFlowFactory.createLazyFlowSubject<BoxesFilter, List<BoxAndSettings>>(any())
        } returns lazyFlowSubject

        boxesRepository = BoxesRepository(
            accountsRepository = accountsRepository,
            boxesSource = boxesSource,
            lazyFlowFactory = lazyFlowFactory
        )

        mockkStatic("com.mcmouse88.okhttp.domain.ExceptionsKt")
    }

    @After
    fun cleanUp() {
        unmockkAll()
    }

    @Test
    fun initLoadBoxes() = runTest {
        val expectedListFromSource = listOf(
            createBoxAndSettings(id = 1, name = "Red"),
            createBoxAndSettings(id = 2, name = "Green")
        )
        coEvery {
            boxesSource.getBoxes(BoxesFilter.ONLY_ACTIVE)
        } returns expectedListFromSource

        val fetcherSlot = arrangeRepositoryWithLazyFlowSlot()

        val loader = fetcherSlot.captured
        val list = loader(BoxesFilter.ONLY_ACTIVE)

        Assert.assertEquals(expectedListFromSource, list)
    }

    @Test
    fun initUserDefaultBackendExceptionsWrapper() = runTest {
        val expectedListFromSource = listOf(
            createBoxAndSettings(id = 1, name = "Red"),
            createBoxAndSettings(id = 2, name = "Green")
        )

        coEvery {
            wrapBackendException<List<BoxAndSettings>>(any())
        } returns expectedListFromSource

        val fetcherSlot = arrangeRepositoryWithLazyFlowSlot()
        val loader = fetcherSlot.captured
        val list = loader(BoxesFilter.ONLY_ACTIVE)

        Assert.assertEquals(expectedListFromSource, list)
    }

    @Test
    fun getBoxesWithSuccessAccountListenBoxes() = runTest {
        val expectedBoxes = listOf(
            createBoxAndSettings(id = 1),
            createBoxAndSettings(id = 2)
        )

        every { accountsRepository.getAccount() } returns flowOf(Success(createAccount(id = 1)))
        coEvery { lazyFlowSubject.listen(any()) } returns flowOf(Success(expectedBoxes))

        val collectedResult = boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).toList()

        Assert.assertEquals(1, collectedResult.size)
        val boxes = collectedResult.first()
        Assert.assertEquals(expectedBoxes, boxes.getValueOrNull())
    }

    @Test
    fun getBoxesWithoutSuccessAccountMapsAccountResult() = runTest {
        val exception = IllegalStateException()
        every { accountsRepository.getAccount() } returns
                flowOf(
                    Error(exception),
                    Empty(),
                    Pending()
                )

        val collectedResult = boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).toList()

        Assert.assertEquals(3, collectedResult.size)
        Assert.assertEquals(exception, (collectedResult[0] as Error).error)
        Assert.assertTrue(collectedResult[1] is Empty)
        Assert.assertTrue(collectedResult[2] is Pending)
    }

    @Test
    fun getBoxesListensForFurtherAccountChanges() = runTest {
        val boxesForAccount1 = listOf(
            createBoxAndSettings(id = 1)
        )
        val boxesForAccount2 = listOf(
            createBoxAndSettings(id = 2),
            createBoxAndSettings(id = 3)
        )

        every { lazyFlowSubject.listen(any()) } returns
                flowOf(Pending(), Success(boxesForAccount1)) andThen
                flowOf(Pending(), Success(boxesForAccount2))

        every { accountsRepository.getAccount() } returns flowOf(
            Success(createAccount(id = 1)),
            Success(createAccount(id = 2))
        )

        val collectedResult = boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).toList()

        Assert.assertEquals(4, collectedResult.size)
        Assert.assertTrue(collectedResult[0] is Pending)
        Assert.assertEquals(boxesForAccount1, collectedResult[1].getValueOrNull())
        Assert.assertTrue(collectedResult[2] is Pending)
        Assert.assertEquals(boxesForAccount2, collectedResult[3].getValueOrNull())
    }

    @Test
    fun reloadWithFailedAccountResultReloadsAccountData() = runTest {
        every { accountsRepository.getAccount() } returns flowOf(Error(IllegalStateException()))
        every { accountsRepository.reloadAccount() } just runs

        boxesRepository.getBoxesAndSettings(BoxesFilter.ONLY_ACTIVE).collect()
        boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)

        verify(exactly = 1) { accountsRepository.reloadAccount() }
    }

    @Test
    fun reloadWithAnyOtherAccountResultReloadsBoxesFlow() = runTest {
        every { accountsRepository.getAccount() } returns flowOf(Success(createAccount(id = 1)))
        every { lazyFlowSubject.listen(any()) } returns flowOf()
        every { lazyFlowSubject.reloadArgument(any(), any()) } just runs

        boxesRepository.getBoxesAndSettings(BoxesFilter.ALL).collect()
        boxesRepository.reload(BoxesFilter.ONLY_ACTIVE)

        verify(exactly = 1) { lazyFlowSubject.reloadArgument(BoxesFilter.ONLY_ACTIVE) }
    }

    @Test
    fun activateBoxActivatesBoxAndReloadsBoxesFlow() = runTest {
        val box = createBox(id = 7)
        every { lazyFlowSubject.reloadAll(any()) } just runs
        coEvery { boxesSource.setIsActive(any(), any()) } just runs

        boxesRepository.activateBox(box)

        coVerifyOrder {
            boxesSource.setIsActive(box.id, true)
            lazyFlowSubject.reloadAll(silentMode = true)
        }
    }

    @Test
    fun activateBoxWith401ErrorThrowsAuthException() = runTest {
        coEvery { boxesSource.setIsActive(any(), any()) } throws
                BackendException(code = 401, message = "Oops")

        catch<AuthException> { boxesRepository.activateBox(createBox()) }
        wellDone()
    }

    @Test
    fun deactivateBoxDeactivatesBoxAndReloadsBoxesFlow() = runTest {
        val box = createBox(id = 7)
        every { lazyFlowSubject.reloadAll(any()) } just runs
        coEvery { boxesSource.setIsActive(any(), any()) } just runs

        boxesRepository.deactivateBox(box)

        coVerifyOrder {
            boxesSource.setIsActive(box.id, false)
            lazyFlowSubject.reloadAll(silentMode = true)
        }
    }

    @Test
    fun deactivateBoxWith401ErrorThrowsAuthException() = runTest {
        coEvery { boxesSource.setIsActive(any(), any()) } throws
                BackendException(code = 401, message = "Oops")

        catch<AuthException> { boxesRepository.deactivateBox(createBox()) }
        wellDone()
    }

    private fun arrangeRepositoryWithLazyFlowSlot(
    ): CapturingSlot<SuspendValueLoader<BoxesFilter, List<BoxAndSettings>>> {
        val factory: LazyFlowFactory = mockk()
        val slot = slot<SuspendValueLoader<BoxesFilter, List<BoxAndSettings>>>()
        every { factory.createLazyFlowSubject(capture(slot)) } returns mockk()
        BoxesRepository(accountsRepository, boxesSource, factory)
        return slot
    }
}