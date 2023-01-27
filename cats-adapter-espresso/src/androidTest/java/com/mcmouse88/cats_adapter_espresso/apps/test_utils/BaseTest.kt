package com.mcmouse88.cats_adapter_espresso.apps.test_utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.rules.FakeImageLoaderRule
import com.mcmouse88.cats_adapter_espresso.apps.test_utils.rules.TestViewModelScopeRule
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Базовый класс, в котором формируются все правила тестирования, которые мы создали, а также те
 * правила которые уже были в библиотеках, которые нам нужны для тестирования. Например
 * [TestViewModelScopeRule] подменяет main диспатчер для корутин, [InstantTaskExecutorRule]
 * подменяет способ оповещения [LiveData], чтобы данные передавали напрямую без переключения
 * потоков, [MockKRule] подставляет зависимости с аннотациями библиотеки MockK, [HiltAndroidRule]
 * если в проекте есть Hilt и нужно подменять модули тестовыми модулями, и можнго было инжектить
 * зависимости прямо в тестовые классы, пример:
 * ```kotlin
 * @Inject
 * lateinit var catsRepository: CatsRepository
 * ```
 * Но чтобы это работало нужно прописать следующее:
 * ```kotlin
 * @Before
 * open fun setUp() {
 *    hiltRule.inject()
 * }
 * ```
 * Вызов команды [hiltRule.inject()] и произведет инициализацию всех зависимостей, помеченных
 * аннотацией [Inject]
 */
open class BaseTest {

    @get:Rule
    val testViewModelScopeRule = TestViewModelScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @Inject
    lateinit var catsRepository: CatsRepository

    @Before
    open fun setUp() {
        hiltRule.inject()
    }
}