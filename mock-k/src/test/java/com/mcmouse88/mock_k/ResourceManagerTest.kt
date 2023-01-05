package com.mcmouse88.mock_k

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executor

class ResourceManagerTest {

    interface Bar {
        fun print(n: Int): Boolean
        fun foo()
        fun getInt(): Int
        fun getBoolean(): Boolean
    }

    /**
     * Также для создания объектов мокков можно использовать аннотации. Аннотация [MockK] создает
     * простой объект мокк (аналог вызова метода [mockk] без параметра [relaxed]), аннотация
     * [RelaxedMockK] создает объект с параметром [relaxed] равным true, и аннотация [InjectMockKs]
     * создаст объект из мокков уже объявленных выше специальными аннотациями. Для того, чтобы
     * все эти аннотации работали необходимо указать аннотацию [Rule]. Чтобы задать реализацию
     * объектам созданным при помощи аннотации нужно также использовать методы [every], [answers] и
     * [returns]. Также можно еще использовать метод [andThen], который при повторном вызове
     * метода уже вернет это значение (количество вызовов [andThen] не ограничено). Пример:
     * ```kotlin
     * every { bar.getInt() } returns 42 andThen 37 andThen 23
     * ```
     *
     * Также вместо [andThen] можно использовать метод [returnsMany] который возвращает список
     * значений, который будет возвращать метод поочередно. Для того, чтобы для [Unit] метода
     * оставыть пустую реализацию (в случае использования простых мокков) нужно написать
     * [just runs] после вызова метода [every], пример:
     * ```kotlin
     * every { bar.foo() } just runs
     * ```
     */
    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var executor: Executor

    @MockK
    lateinit var errorHandler: ErrorHandler<String>

    @RelaxedMockK
    lateinit var bar: Bar

    @InjectMockKs
    lateinit var resourceManager: ResourceManager<String>

    /**
     * Для создания тестового объекта используется метод [mockk]. Чтобы создать шпиона (обертка
     * над существующим классом, реализуюшая паттерн декораторб когда мы вызываем метод шпиона, то
     * по факту вызываем метод у реального класса, но при этом шпион запоминает этот вызов),
     * используется метод [spyk]. В тесте можно проверить был ли вызван метод или не был вызыван
     * с использованием метода [verify] (который работает только с объектами-шпионами). Кроме того
     * шпионы позволят заменить реальное поведение реального объекта. Также при помощи метода
     * [every] и метода [answers] можно подменить реалтзацию метода существующего класса. Примеры
     * исплользования шпионов:
     * ```kotlin
     * val resourceManagerSpy = spyk(resourceManager)
     *
     * every { resourceManagerSpy.destroy() } answers {
     *    println("destroy() call has been replaced by the spy")
     * }
     *
     * resourceManagerSpy.destroy()
     *
     * verify {
     *    resourceManagerSpy.destroy()
     * }
     * ```
     *
     * При создании мокков можно указать аргумент [relaxed], если его не указывать, то мокк
     * будет обычным, и при попытке вызова метода мокка мы получим ошибку, так как реализация
     * метода у него отсутствует, если же указать ***relaxed = true*** то тогда [Mockk] попробует
     * создать реализацию каждого метода по умолчанию. Это озночает что [Unit] методы ничего
     * не будут делать, а если методы возвращают какое-либо значение (как правило примитивные типы)
     * то будут возвращать какие-либо значения (Пример: для int -> 0, boolean -> false).
     * Пример создания мокков без аннотации при помощи метода [mockk]:
     * ```kotlin
     * val testExecutor = mockk<Executor>(relaxed = true)
     * val testErrorHandler = mockk<ErrorHandler<String>>()
     * val resourceManager = ResourceManager(
     *    executor = testExecutor,
     *    errorHandler =  testErrorHandler
     * )
     *
     * val bar = mockk<Bar>()
     * ```
     *
     * Такжи при создании реализации определнного метода при использовании [every] и [answers]
     * при каждом вызове этого метода с любым аргументом (в примере [any()]) будет выполняться
     * блок кода. При помощи метода [firstArg] и т.д. можно обращаться к аргументам функции,
     * либо просто функции [arg()], в параметр которой передать Int с номером его позиции. Также
     * в мокках при передачи объектов в тестируемые функции используются так называемые матчеры
     * ([any()] - в данном случае матчер). При помощи матчеров можно определять поведение и
     * реализацию функции в зависимости от переданного матчера.
     */
    @Test
    fun test() {
        every { bar.foo() } answers {
            println("foo() method has been called")
        }

        every { executor.execute(any()) } answers {
            firstArg<Runnable>().run()
        }

        every { bar.getInt() } returnsMany listOf(42, 37, 23)
        every { bar.getBoolean() } returns true

        every { bar.print(less(0)) } returns false
        every { bar.print(more(10, andEquals = true)) } answers {
            println(firstArg<Int>())
            true
        }

        println("Arg -1, returns: ${bar.print(-1)}")
        println("Arg 10, returns: ${bar.print(10)}")
    }

    /**
     * Также еще есть матчеры перехватчики, которые запоминают полученные значения, и сохраняют
     * их в спец. слот, пример матчера [capture]. Если использовать обычный метод [slot], то он
     * будет хранить в себе только одно значение из самого последнего вызова. Пример:
     * ```kotlin
     * val intSlot = slot<Int>()
     * every { bar.print(capture(intSlot)) } returns true
     * println("Captured arg: ${intSlot.captured}")
     * ```
     *
     * Если же нам нужно знать всю историю вызовов, то нужно заменить [slot] на обычный
     * [MutableList].
     */
    @Test
    fun test2() {
        val listSlot = mutableListOf<Int>()

        every { bar.print(capture(listSlot)) } returns true

        bar.print(123)
        bar.print(48)
        bar.print(19)

        println("Captured arg: ${listSlot.joinToString()}")
    }

    /**
     * Чтобы проверить результаты мокк теста используются так называемые валидаторы, один из них
     * является [verify]. Для того, чтобы определить сколько раз вызывался метод(лямбда) (нам в
     * данном тесте нужно чтобы этот метод(лямбда) вызывался только один раз), у метода [verify]
     * есть специальные параметры для этого:
     * - atMost -> метод(лямбда) должен быть вызван максимум <количесво раз> указанных в параметре
     * - atLeast -> метод(лямбда) должен быть вызван минимум <количесво раз> указанных в параметре
     * - exactly -> метод(лямбда) должен быть вызван точно <количесво раз> указанных в параметре
     *
     * Но так как нам нужно проверить не только сколько раз был вызван метод(лямбда), но и также
     * проверить ожидаемое значение, для этого можно использовать метод [confirmVerified], в который
     * мы передадим объект consumer, теперь мы тестируем не только количесво вызововов
     * метода(лямбды), но и ожидаемое значение. Пример:
     * ```Kotlin
     * verify(exactly = 1) {
     *   consumer(TEST)
     * }
     * confirmVerified(consumer)
     * ```
     *
     * Также вместо этой комбинации можно использовать метод [verifySequence]. Перечень валидаторов:
     * - [verify] -> Самый простой, который проверяет, что методы внутри блока [verify] были
     * когда-то вызваны
     * - [verifyOrder] -> более строгий, проверяет правильность порядка вызова
     * - [verifyAll] -> не проверяет порядок вызова, но проверяет, чтобы не было сторонних вызовов,
     * кроме как взаимодействия с мокками помимо тех, которые были описаны в блоке метода
     * [verifyAll], но в нем нельзя указать точное количество вызовов
     * - [verifySequence] -> самый жесткий валидатор, проверяет одновременно, что все методы были
     * вызваны в нужном порядке, нужное количество раз, и что были вызваны только они, и никакие
     * другие методы.
     */
    @Test
    fun consumeResourceAfterSetResourceCallReceivesResource() {
        // arrange
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        // act
        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer)

        // assert
        verifySequence {
            consumer(TEST)
        }
    }

    @Test
    fun consumeResourceCallsAfterSetResourceCallReceiveResourceForEachConsumer() {
        // arrange
        val resourceManager = createResourceManager()
        val consumer1 = createConsumer()
        val consumer2 = createConsumer()

        // act
        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer1)
        resourceManager.consumeResource(consumer2)

        // assert
        verifySequence {
            consumer1(TEST)
            consumer2(TEST)
        }
    }

    @Test
    fun consumeResourceAfterSetResourceCallsReceivesLatestResource() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST1)
        resourceManager.setResource(TEST2)
        resourceManager.consumeResource(consumer)

        verify(exactly = 1) { consumer(TEST2) }
        confirmVerified(consumer)
    }

    @Test
    fun consumeResourceCallsWithSameConsumerCanReceiveTheSameResource() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer)
        resourceManager.consumeResource(consumer)

        verify(exactly = 2) { consumer(TEST) }
        confirmVerified(consumer)
    }

    /**
     * Для того, чтобы протестировать ситуация, когда метод(лямбда) не вызывался, используется
     * метод [wasNot called]
     */
    @Test
    fun consumeResourceWithoutActiveResourceDoesNothing() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)

        verify { consumer wasNot called }
    }

    @Test
    fun setResourceAfterConsumeResourceCallDeliversResourceToConsumer() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST)

        verify(exactly = 1) { consumer(TEST) }
        confirmVerified(consumer)
    }

    @Test
    fun consumeResourceReceivesResourceOnlyOnce() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST1)
        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST2)

        verify(exactly = 1) { consumer(TEST1) }
        confirmVerified(consumer)
    }

    @Test
    fun consumeResourceCallsWithSameConsumerCanReceiveMultipleResources() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST1)
        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST2)
        resourceManager.consumeResource(consumer)


        verifySequence {
            consumer(TEST1)
            consumer(TEST2)
        }
    }

    @Test
    fun setResourceAfterMultipleConsumeResourceCallsDeliversResourceToAllConsumers() {
        val resourceManager = createResourceManager()
        val consumer1 = createConsumer()
        val consumer2 = createConsumer()

        resourceManager.consumeResource(consumer1)
        resourceManager.consumeResource(consumer2)
        resourceManager.setResource(TEST)

        verifySequence {
            consumer1(TEST)
            consumer2(TEST)
        }
    }

    @Test
    fun setResourceCallsAfterConsumeResourceCallDeliversTheFirstResourceOnce() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST1)
        resourceManager.setResource(TEST2)

        verify(exactly = 1) { consumer(TEST1) }
        confirmVerified(consumer)
    }

    @Test
    fun setResourceBetweenConsumeResourceCallsDeliversTheSameResourceToAllConsumers() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer)

        verify(exactly = 2) { consumer(TEST) }
        confirmVerified(consumer)
    }

    @Test
    fun setResourceDoubleCallBetweenConsumeResourceCallsDeliversDifferentResources() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST1)
        resourceManager.setResource(TEST2)
        resourceManager.consumeResource(consumer)

        verifySequence {
            consumer(TEST1)
            consumer(TEST2)
        }
    }

    @Test
    fun consumeResourceAfterClearResourceCallDoesNothing() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST)
        resourceManager.clearResource()
        resourceManager.consumeResource(consumer)

        verify { consumer wasNot called }
    }

    @Test
    fun consumeResourceAfterClearResourceAndSetResourceCallsReceivesLatestResource() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST1)
        resourceManager.clearResource()
        resourceManager.setResource(TEST2)
        resourceManager.consumeResource(consumer)

        verify(exactly = 1) { consumer(TEST2) }
        confirmVerified(consumer)
    }

    @Test
    fun setResourceAfterConsumeResourceAndClearResourceCallsDeliversLatestResource() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST1)
        resourceManager.clearResource()
        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST2)

        verify(exactly = 1) { consumer(TEST2) }
        confirmVerified(consumer)
    }

    @Test
    fun destroyClearsCurrentResource() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.setResource(TEST)
        resourceManager.destroy()
        resourceManager.consumeResource(consumer)

        verify { consumer wasNot called }
    }

    @Test
    fun destroyClearsPendingConsumers() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)
        resourceManager.destroy()
        resourceManager.setResource(TEST)

        verify { consumer wasNot called }
    }

    @Test
    fun setResourceAfterDestroyCallDoesNothing() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.destroy()
        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer)

        verify { consumer wasNot called }
    }

    @Test
    fun consumeResourceAfterDestroyCallDoesNothing() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.destroy()
        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST)

        verify { consumer wasNot called }
    }

    @Test(expected = Test.None::class)
    fun setResourceHandlesConcurrentConsumersModification() {
        val resourceManager = createResourceManager()
        val consumer = createConsumer()

        resourceManager.consumeResource {
            resourceManager.clearResource()
            resourceManager.consumeResource(consumer)
        }
        resourceManager.setResource(TEST)

        verify(exactly = 1) { consumer(TEST) }
        confirmVerified(consumer)
    }

    @Test
    fun consumeResourceDeliversExceptionsToErrorHandler() {
        val errorHandler:ErrorHandler<String> = mockk()
        every { errorHandler.onError(any(), any()) } just runs
        val resourceManager = createResourceManager(
            errorHandler = errorHandler
        )
        val expectedException = IllegalStateException(TEST_EXCEPTION)

        resourceManager.setResource(TEST)
        resourceManager.consumeResource {
            throw expectedException
        }

        verify(exactly = 1) {
            errorHandler.onError(refEq(expectedException), TEST)
        }
        confirmVerified(errorHandler)
    }

    @Test
    fun setResourceDeliversExceptionsToErrorHandler() {
        val errorHandler: ErrorHandler<String> = mockk()
        every { errorHandler.onError(any(), any()) } just runs
        val resourceManager = createResourceManager(
            errorHandler = errorHandler
        )
        val expectedException = IllegalStateException(TEST_EXCEPTION)

        resourceManager.consumeResource {
            throw expectedException
        }
        resourceManager.setResource(TEST)

        verify(exactly = 1) {
            errorHandler.onError(refEq(expectedException), TEST)
        }
        confirmVerified(errorHandler)
    }

    @Test
    fun consumeResourceDoesNotInvokeConsumerOutsideOfExecutor() {
        val executor: Executor = mockk()
        every { executor.execute(any()) } just runs
        val resourceManager = createResourceManager(
            executor = executor
        )
        val consumer = createConsumer()

        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer)

        verify(exactly = 1) {
            executor.execute(any())
            consumer wasNot called
        }
        confirmVerified(executor, consumer)
    }

    @Test
    fun consumeResourceInvokesConsumerInExecutor() {
        val executor: Executor = mockk()
        val commandSlot = slot<Runnable>()
        every { executor.execute(capture(commandSlot)) } just runs
        val resourceManager = createResourceManager(
            executor = executor
        )
        val consumer = createConsumer()

        resourceManager.setResource(TEST)
        resourceManager.consumeResource(consumer)

        Assert.assertTrue(commandSlot.isCaptured)
        commandSlot.captured.run()
        verify(exactly = 1) { consumer(TEST) }
        confirmVerified(consumer)
    }

    @Test
    fun setResourceInvokesPendingConsumerInExecutor() {
        val executor: Executor = mockk()
        val commandSlot = slot<Runnable>()
        every { executor.execute(capture(commandSlot)) } just runs
        val resourceManager = createResourceManager(
            executor = executor
        )
        val consumer = createConsumer()

        resourceManager.consumeResource(consumer)
        resourceManager.setResource(TEST)

        Assert.assertTrue(commandSlot.isCaptured)
        commandSlot.captured.run()
        verify(exactly = 1) { consumer(TEST) }
        confirmVerified(consumer)
    }


    private fun createResourceManager(
        executor: Executor = immediateExecutor(),
        errorHandler: ErrorHandler<String> = dummyErrorHandler()
    ): ResourceManager<String> {
        return ResourceManager(executor, errorHandler)
    }

    /**
     * Dummy объект, или объект кукла, который ничего не делает, и представляет собой заглушку для
     * объекта класса необходимого для создания тестируемого объекта.
     */
    private fun dummyErrorHandler(): ErrorHandler<String> = mockk()

    private fun immediateExecutor(): Executor {
        val executor = mockk<Executor>()
        every { executor.execute(any()) } answers {
            firstArg<Runnable>().run()
        }

        return executor
    }

    private fun createConsumer(): Consumer<String> = mockk(relaxed = true)

    private companion object {
        const val TEST = "TEST"
        const val TEST1 = "TEST1"
        const val TEST2 = "TEST2"
        const val TEST_EXCEPTION = "Test exception"
    }
}