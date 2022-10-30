package com.mcmouse88.okhttp.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.mcmouse88.okhttp.test_utils.arranged
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verifySequence
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SharedPreferencesAppSettingsTest {

    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var preference: SharedPreferences

    @RelaxedMockK
    lateinit var editor: SharedPreferences.Editor

    private lateinit var settings: SharedPreferencesAppSettings

    private val expectedTokenKey = "currentToken"

    @Before
    fun setUp() {
        every { context.getSharedPreferences(any(), any()) } returns preference
        every { preference.edit() } returns editor
        settings = SharedPreferencesAppSettings(context)
    }

    @Test
    fun setCurrentTokenPutsValueToPreference() {
        arranged()

        settings.setCurrentToken("token")

        verifySequence {
            preference.edit()
            editor.putString(expectedTokenKey, "token")
            editor.apply()
        }
    }

    @Test
    fun setCurrentTokenWithNullRemovesValueFromPreferences() {
        arranged()

        settings.setCurrentToken(null)

        verifySequence {
            preference.edit()
            editor.remove(expectedTokenKey)
            editor.apply()
        }
    }

    @Test
    fun getCurrentTokenReturnsValueFromPreferences() {
        every { preference.getString(any(), any()) } returns "token"

        val token = settings.getCurrentToken()

        Assert.assertEquals("token", token)
    }
}