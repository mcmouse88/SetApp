package com.mcmouse.nav_tabs

import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.InMemoryAccountsRepository
import com.mcmouse.nav_tabs.models.boxes.BoxesRepository
import com.mcmouse.nav_tabs.models.boxes.InMemoryBoxesRepository

object Repositories {

    val accountsRepository: AccountsRepository = InMemoryAccountsRepository()

    val boxesRepository: BoxesRepository = InMemoryBoxesRepository(accountsRepository)
}