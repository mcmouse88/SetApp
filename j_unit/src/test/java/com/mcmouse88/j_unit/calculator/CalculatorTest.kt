package com.mcmouse88.j_unit.calculator

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CalculatorTest {

    private val delta = 0.00001

    private lateinit var calculator: Calculator

    /**
     * Аннотациями [Before] и [After] помечаются функции, которые должны быть выполнены до каждого
     * теста и после каждого теста, в методе, помеченном аннотацией [Before] обычно инициализируют
     * ресурсы, а в методе [After] удаляют/очищают.
     */
    @Before
    fun setup() {
        calculator = Calculator()
        calculator.init()
    }

    @After
    fun cleanUp() {
        calculator.destroy()
    }

    /**
     * Тест как правило состоит из трех частей, первая это инициализация переменных, вторая это
     * сам тест, и третья проверка результатов теста.
     */
    @Test(expected = IllegalStateException::class)
    fun divideWithZeroDivisorThrowIllegalStateException() {
        // arrange
        // -

        // act
        calculator.divide(10.0, 0.0)

        // assert
        // -
    }

    @Test
    fun subtractCalculateSubtraction() {
        // arrange
        val calculator = Calculator()
        calculator.init()

        // act
        val result = calculator.subtract(3.0, 2.0)

        // assert
        Assert.assertEquals(1.0, result, delta)
    }

    @Test
    fun sumCalculateSum() {
        // arrange
        // -

        // act
        val result = calculator.sum(2.0, 2.0)

        // assert
        Assert.assertEquals(4.0, result, delta)
    }
}