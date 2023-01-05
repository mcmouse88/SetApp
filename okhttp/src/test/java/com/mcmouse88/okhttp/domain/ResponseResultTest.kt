package com.mcmouse88.okhttp.domain

import com.mcmouse88.okhttp.test_utils.catch
import com.mcmouse88.okhttp.test_utils.wellDone
import org.junit.Assert
import org.junit.Test

class ResponseResultTest {

    @Test
    fun getValueOrNullReturnsNullForNonSuccessResult() {
        val emptyResult = Empty<String>()
        val pendingResult = Pending<String>()
        val errorResult = Error<String>(Exception())

        val emptyValue = emptyResult.getValueOrNull()
        val pendingValue = pendingResult.getValueOrNull()
        val errorValue = errorResult.getValueOrNull()

        Assert.assertNull(emptyValue)
        Assert.assertNull(pendingValue)
        Assert.assertNull(errorValue)
    }

    @Test
    fun getValueOrNullReturnsValueForSuccessResult() {
        val successResult = Success("Test")

        val value = successResult.getValueOrNull()

        Assert.assertEquals("Test", value)
    }

    @Test
    fun isFinishedForSuccessAndErrorReturnsTrue() {
        val errorResult = Error<String>(Exception())
        val successResult = Success("Test")

        val isErrorFinished = errorResult.isFinished()
        val isSuccessFinished = successResult.isFinished()

        Assert.assertTrue(isErrorFinished)
        Assert.assertTrue(isSuccessFinished)
    }

    @Test
    fun isFinishedForEmptyAndPendingReturnsFalse() {
        val emptyResult = Empty<String>()
        val pendingResult = Pending<String>()

        val isEmptyFinished = emptyResult.isFinished()
        val isPendingFinished = pendingResult.isFinished()

        Assert.assertFalse(isEmptyFinished)
        Assert.assertFalse(isPendingFinished)
    }

    @Test
    fun testNonSuccessResultsMapping() {
        val exception = Exception()
        val emptyResult = Empty<String>()
        val pendingResult = Pending<String>()
        val errorResult = Error<String>(exception)

        val mappedEmptyResult = emptyResult.mapResult<Int>()
        val mappedPendingResult = pendingResult.mapResult<Int>()
        val mappedErrorResult = errorResult.mapResult<Int>()

        Assert.assertTrue(mappedEmptyResult is Empty<Int>)
        Assert.assertTrue(mappedPendingResult is Pending<Int>)
        Assert.assertTrue(mappedErrorResult is Error<Int>)
        Assert.assertSame(exception, (mappedErrorResult as Error<Int>).error)
    }

    @Test
    fun mapWithoutMapperCantConvertSuccessResult() {
        val result = Success("Test")

        catch<IllegalStateException> { result.mapResult<Int>() }
        wellDone()
    }

    @Test
    fun mapWithMapperConvertSuccessToResult() {
        val result = Success("123")

        val mappedResult = result.mapResult { it.toInt() }

        Assert.assertTrue(mappedResult is Success<Int>)
        Assert.assertEquals(123, (mappedResult as Success<Int>).value)
    }

    @Test
    fun testEquals() {
        val exception = IllegalStateException()
        val pending1 = Pending<String>()
        val pending2 = Pending<String>()
        val empty1 = Empty<String>()
        val empty2 = Empty<String>()
        val error1 = Error<String>(exception)
        val error2 = Error<String>(exception)
        val success1 = Success("val")
        val success2 = Success("val")

        Assert.assertEquals(pending1, pending2)
        Assert.assertEquals(empty1, empty2)
        Assert.assertEquals(error1, error2)
        Assert.assertEquals(success1, success2)
    }

    @Test
    fun testNotEquals() {
        val pending = Pending<String>()
        val empty = Empty<String>()
        val error1 = Error<String>(IllegalStateException())
        val error2 = Error<String>(IllegalStateException())
        val success1 = Success("val1")
        val success2 = Success("val2")

        Assert.assertNotEquals(pending, empty)
        Assert.assertNotEquals(pending, error1)
        Assert.assertNotEquals(pending, success1)
        Assert.assertNotEquals(empty, error1)
        Assert.assertNotEquals(empty, success1)
        Assert.assertNotEquals(error1, error2)
        Assert.assertNotEquals(error1, success1)
        Assert.assertNotEquals(success1, success2)
    }

    @Test
    fun testHashCode() {
        val exception = IllegalStateException()
        val pending1 = Pending<String>()
        val pending2 = Pending<String>()
        val empty1 = Empty<String>()
        val empty2 = Empty<String>()
        val error1 = Error<String>(exception)
        val error2 = Error<String>(exception)
        val success1 = Success("val")
        val success2 = Success("val")

        Assert.assertEquals(pending1.hashCode(), pending2.hashCode())
        Assert.assertEquals(empty1.hashCode(), empty2.hashCode())
        Assert.assertEquals(error1.hashCode(), error2.hashCode())
        Assert.assertEquals(success1.hashCode(), success2.hashCode())
    }
}