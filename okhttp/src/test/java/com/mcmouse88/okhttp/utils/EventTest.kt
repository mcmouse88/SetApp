package com.mcmouse88.okhttp.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class EventTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    /**
     * Первый метод проверяет, что [Event] возвращаает значение только один, а при повторном
     * обращении возвращает null.
     */
    @Test
    fun getReturnsValueOnlyOnce() {
        val event = Event("some-value")

        val value1 = event.get()
        val value2 = event.get()

        Assert.assertEquals("some-value", value1)
        Assert.assertNull(value2)
    }

    @Test
    fun publishEventSendsEventToLiveData() {
        val mutableLiveEvent = MutableLiveEvent<String>()
        val liveEvent = mutableLiveEvent.share()

        mutableLiveEvent.publishEvent("value")

        val event = liveEvent.value
        Assert.assertEquals("value", event?.get())
    }

    @Test
    fun observeEventListensForEvent() {
        val mutableLiveEvent = MutableLiveEvent<String>()
        val listener = mockk<EventListener<String>>(relaxed = true)
        val liveEvent = mutableLiveEvent.share()
        val lifecycleOwner = createLifecycleOwner()

        mutableLiveEvent.publishEvent("value")
        liveEvent.observeEvent(lifecycleOwner, listener)

        verify(exactly = 1) {
            listener.invoke("value")
        }
        confirmVerified(listener)
    }

    @Test
    fun observeEventOnUnitListensForEvent() {
        val mutableLiveEvent = MutableUnitLiveEvent()
        val listener = mockk<UnitEventListener>(relaxed = true)
        val liveEvent = mutableLiveEvent.share()
        val lifecycleOwner = createLifecycleOwner()

        mutableLiveEvent.publishEvent(Unit)
        liveEvent.observeEvent(lifecycleOwner, listener)

        verify(exactly = 1) { listener.invoke() }
        confirmVerified(listener)
    }

    /**
     * Метод для имитации жизненного цикла
     */
    private fun createLifecycleOwner(): LifecycleOwner {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifeCycle = mockk<Lifecycle>()
        every { lifeCycle.currentState } returns Lifecycle.State.STARTED
        every { lifeCycle.addObserver(any()) } answers {
            firstArg<LifecycleEventObserver>().onStateChanged(lifecycleOwner, Lifecycle.Event.ON_START)
        }
        every { lifecycleOwner.lifecycle } returns lifeCycle
        return lifecycleOwner
    }
}