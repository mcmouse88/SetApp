package com.mcmouse.nav_tabs.models.accounts.entities

import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings

data class AccountFullData(
    val account: Account,
    val boxAndSettings: List<BoxAndSettings>
)
