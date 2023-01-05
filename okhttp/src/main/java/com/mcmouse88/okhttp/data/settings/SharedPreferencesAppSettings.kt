package com.mcmouse88.okhttp.data.settings

import android.content.Context
import com.mcmouse88.okhttp.domain.settings.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Также у hilt имеются встроенные квалификаторы. Квалификаторы используются для указания hilt
 * какую сущность использовать для внедрения зависимости в конструктор, в данном случае
 * используется квалификатор [ApplicationContext], есть также квалификатор [ActivityContext] и т.д.
 */
class SharedPreferencesAppSettings @Inject constructor(
    @ApplicationContext appContext: Context
) : AppSettings {

    private val sharedPreferences =
        appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun getCurrentToken(): String? {
        return sharedPreferences.getString(PREF_CURRENT_ACCOUNT_TOKEN, null)
    }

    override fun setCurrentToken(token: String?) {
        val editor = sharedPreferences.edit()
        if (token == null) editor.remove(PREF_CURRENT_ACCOUNT_TOKEN)
        else editor.putString(PREF_CURRENT_ACCOUNT_TOKEN, token)
        editor.apply()
    }

    companion object {
        private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
    }
}