package com.mcmouse88.okhttp.test_utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Вспомогательный класс, который задает правила тестирования [ViewModel], его задача подменить
 * [viewModelScope], а именно подменить [Main Dispatcher], который переводит выполнение в главный поток
 * с помощью handler, но после подмены диспатчер будет другой и все корутины не будут использовать
 * [handler] и [looper], а будут выполняться сразу же в текущем потоке. Класс должен наследоваться от
 * [TestWatcher], и в нем нужно переопределить два метода:
 * 1. Устанавливает тест диспатчер при старте теста
 * 2. Восстанавливает Main Dispatcher по умолчанию после окончания теста.
 * [UnconfinedTestDispatcher] специальный диспатчер, который не переводит выполнение кода в другие
 * потоки, выполняет его сразу на текущем потоке, а также игнорирует все задержки.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestViewModelScopeRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}