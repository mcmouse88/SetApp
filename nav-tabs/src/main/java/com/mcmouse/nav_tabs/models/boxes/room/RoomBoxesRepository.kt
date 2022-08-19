package com.mcmouse.nav_tabs.models.boxes.room

import com.mcmouse.nav_tabs.models.AuthException
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.boxes.BoxesRepository
import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings
import com.mcmouse.nav_tabs.models.boxes.room.entities.AccountBoxSettingDbEntity
import com.mcmouse.nav_tabs.models.boxes.room.views.SettingsTuple
import com.mcmouse.nav_tabs.models.room.wrapSQLiteException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*

class RoomBoxesRepository(
    private val boxesDao: BoxesDao,
    private val accountsRepository: AccountsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BoxesRepository {

    override suspend fun getBoxesAndSettings(onlyActive: Boolean): Flow<List<BoxAndSettings>> {
        return accountsRepository.getAccount()
            .flatMapLatest { account ->
                if (account == null) return@flatMapLatest flowOf(emptyList())
                queBoxesAndSettings(account.id)
            }
            .mapLatest { boxAndSetting ->
                if (onlyActive) boxAndSetting.filter { it.isActive }
                else boxAndSetting
            }
    }

    override suspend fun activateBox(box: Box) = wrapSQLiteException(ioDispatcher) {
        setActiveFlagForBox(box, true)
    }

    override suspend fun deactivateBox(box: Box) = wrapSQLiteException(ioDispatcher) {
        setActiveFlagForBox(box, false)
    }

    private fun queBoxesAndSettings(accountId: Long): Flow<List<BoxAndSettings>> {
        return boxesDao.getBoxesAndSettings(accountId).map { entities ->
            entities.map {
                val boxEntity = it.boxDbEntity
                val settingsEntity = it.settingDbEntities
                BoxAndSettings(
                    box = boxEntity.toBox(),
                    isActive = settingsEntity.settings.isActive
                )
            }
        }
    }

    private suspend fun setActiveFlagForBox(box: Box, isActive: Boolean) {
        val account = accountsRepository.getAccount().first() ?: throw AuthException()
        boxesDao.setActiveFlagForBox(
            AccountBoxSettingDbEntity(
                accountId = account.id,
                boxUserId = box.id,
                setting = SettingsTuple(isActive)
            )
        )
    }
}