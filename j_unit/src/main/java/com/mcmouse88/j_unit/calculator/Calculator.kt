package com.mcmouse88.j_unit.calculator

class Calculator {

    fun init() {
        /* no-op */
    }

    fun destroy() {
        /* no-op */
    }

    fun sum(a: Double, b: Double): Double = a + b

    fun subtract(a: Double, b: Double): Double = a - b

    fun multiply(a: Double, b: Double): Double = a * b

    fun divide(a: Double, b: Double): Double {
        if (b == 0.0) throw IllegalStateException("Division by zero")
        return a / b
    }
}