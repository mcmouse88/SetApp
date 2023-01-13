package com.mcmouse88.okhttp.test_utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mcmouse88.okhttp.domain.accounts.AccountsRepository
import com.mcmouse88.okhttp.utils.logger.Logger
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Rule

/**
 * Опционально можно написать базовый класс для всех тестовых [ViewModel].
 */
open class ViewModelTest {

    /**
     * Правило которое устанавливает созданный [TestViewModelScopeRule], в результате все классы
     * наследованные от [ViewModelTest] будут содержать это рпавило и не будут иметь проблем с
     * корутинами, так как в них уже будет использоваться тестовый скоуп.
     */
    @get:Rule
    val testViewModelScopeRule = TestViewModelScopeRule()

    /**
     * Второе правило исходит из зависимости
     * ```kotlin
     * testImplementation 'androidx.arch.core:core-testing:$version'
     * ```
     * он позволяет из Unit тестов нормально работать с LiveData
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Правило для инициализации MockK, позволяет автоматически обработать аннотиции [MockK]
     */
    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var logger: Logger

    @RelaxedMockK
    lateinit var accountsRepository: AccountsRepository
}