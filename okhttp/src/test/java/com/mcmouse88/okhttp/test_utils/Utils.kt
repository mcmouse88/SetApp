package com.mcmouse88.okhttp.test_utils

import org.junit.Assert
import java.lang.AssertionError

inline fun<reified T : Throwable> catch(block: () -> Unit): T {
    try {
        block()
    } catch(e: Throwable) {
        if (e is T) {
            return e
        } else {
            Assert.fail("Invalid exception type. " +
            "Expected: ${T::class.java.simpleName}, " +
            "Actual: ${e::class.java.simpleName}")
        }
    }
    throw AssertionError("No expected exception")
}

fun wellDone() {
    // indicates test passed successfully
}

fun arranged() {
    // indicates arrange section is empty and thus it's already done
    // Look for the method annotated with @Before annotation if uoe see
    // this call.
}