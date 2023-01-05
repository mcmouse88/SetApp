package com.mcmouse.nav_tabs.models.settings

import android.content.Context

class SharedPreferencesAppSettings(
    appContext: Context
) : AppSettings {

    private val sharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun getCurrentAccountId(): Long = sharedPreferences.getLong(PREF_CURRENT_ACCOUNT_ID, AppSettings.NO_ACCOUNT_ID)

    override fun setCurrentAccountId(accountId: Long) {
        sharedPreferences.edit()
            .putLong(PREF_CURRENT_ACCOUNT_ID, accountId)
            .apply()
    }

    companion object {
        private const val PREF_CURRENT_ACCOUNT_ID = "PREF_CURRENT_ACCOUNT_ID"
    }
}