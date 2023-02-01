package com.mcmouse88.catadapterespresso.robolectric.test_utils.base

import com.mcmouse88.catadapterespresso.robolectric.test_utils.rules.FakeImageLoaderRule
import com.mcmouse88.cats_adapter_espresso.model.CatsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Общий класс для всех тестовых классов, использующих робоэлектрик
 */
open class BaseRobolectricTest : BaseTest() {

    /**
     * Для того, чтобы можно было подменять зависимости используемые библиотекой Hilt
     */
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