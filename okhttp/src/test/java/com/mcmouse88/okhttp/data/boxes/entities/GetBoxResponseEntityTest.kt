package com.mcmouse88.okhttp.data.boxes.entities

import android.graphics.Color
import com.mcmouse88.okhttp.data.boxes.entity.GetBoxResponseEntity
import com.mcmouse88.okhttp.domain.boxes.entities.Box
import com.mcmouse88.okhttp.domain.boxes.entities.BoxAndSettings
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetBoxResponseEntityTest {

    @Before
    fun setUp() {
        mockkStatic(Color::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(Color::class)
    }

    /**
     * В данном примере показано, как можно подменить классы из android SDK, которые нельзя
     * использовать использовать в тестах
     * ```kotlin
     * every { Color.parseColor(any()) } returns Color.RED
     * ```
     * Данный метод класса [Color] к тому же является статическим, и чтобы можно было вызвать
     * статический метод, перед началом теста указываем, что хотим замокать статические методы
     * определенного класса, пример:
     * ```kotlin
     * @Before
     * fun setUp() {
     *    mockkStatic(Color::class)
     * }
     *
     */
    @Test
    fun toBoxAndSettingsMapsToInAppEntity() {
        val responseEntity = GetBoxResponseEntity(
            id = 2,
            colorName = "Red",
            colorValue = "#FF0000",
            isActive = true
        )
        every { Color.parseColor(any()) } returns Color.RED

        val inAppEntity = responseEntity.toBoxAndSettings()

        val expectedAppEntity = BoxAndSettings(
            box = Box(
                id = 2,
                colorName = "Red",
                colorValue = Color.RED
            ),
            isActive = true
        )
        Assert.assertEquals(expectedAppEntity, inAppEntity)
    }
}