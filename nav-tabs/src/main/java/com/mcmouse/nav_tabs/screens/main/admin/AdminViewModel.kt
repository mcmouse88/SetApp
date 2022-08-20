package com.mcmouse.nav_tabs.screens.main.admin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcmouse.nav_tabs.R
import com.mcmouse.nav_tabs.models.accounts.AccountsRepository
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.models.accounts.entities.AccountFullData
import com.mcmouse.nav_tabs.models.boxes.entities.Box
import com.mcmouse.nav_tabs.models.boxes.entities.BoxAndSettings
import com.mcmouse.nav_tabs.utils.resources.Resources
import com.mcmouse.nav_tabs.utils.share
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AdminViewModel(
    private val accountsRepository: AccountsRepository,
    private val resources: Resources
) : ViewModel(), AdminItemsAdapter.Listener {

    private val _items = MutableLiveData<List<AdminTreeItem>>()
    val items = _items.share()

    private val expandedIdentifiers = mutableSetOf(getRootId())
    private val expandedItemsStateFlow = MutableStateFlow(ExpansionsState(expandedIdentifiers))

    init {
        viewModelScope.launch {
            combine(accountsRepository.getAllData(), expandedItemsStateFlow) { allData, expansionState ->
                val rootNode = toNode(allData, expansionState.identifiers)
                flatNodes(rootNode)
            }.collect {
                _items.value = it
            }
        }
    }

    override fun onItemToggled(item: AdminTreeItem) {
        if (item.expansionStatus == ExpansionStatus.NOT_EXPANDABLE) return
        if (item.expansionStatus ==ExpansionStatus.EXPANDED) expandedIdentifiers.remove(item.id)
        else expandedIdentifiers.add(item.id)
        expandedItemsStateFlow.value = ExpansionsState(expandedIdentifiers)
    }

    private fun toNode(accountsDataList: List<AccountFullData>, expandedIdentifiers: Set<Long>): Node {
        val rootException = getExpansionStatus(getRootId(), accountsDataList.isNotEmpty(), expandedIdentifiers)
        return Node(
            id = getRootId(),
            attributes = mapOf(resources.getString(R.string.all_accounts) to ""),
            expansionStatus = rootException,
            nodes = if (rootException != ExpansionStatus.EXPANDED) emptyList() else accountsDataList.map { accountsData ->
                val accountExpansionStatus = getExpansionStatus(getAccountId(accountsData), accountsData.boxAndSettings.isNotEmpty(), expandedIdentifiers)
                Node(
                    id = getAccountId(accountsData),
                    attributes = accountToMap(accountsData.account),
                    expansionStatus = accountExpansionStatus,
                    nodes = if (accountExpansionStatus != ExpansionStatus.EXPANDED) emptyList() else accountsData.boxAndSettings.map { boxAndSettings ->
                        val boxExpansionStatus = getExpansionStatus(getBoxId(boxAndSettings), true, expandedIdentifiers)
                        Node(
                            id = getBoxId(boxAndSettings),
                            attributes = boxToMap(boxAndSettings.box),
                            expansionStatus = boxExpansionStatus,
                            nodes = if (boxExpansionStatus != ExpansionStatus.EXPANDED) emptyList() else listOf(
                                Node(
                                    id = getSettingsId(boxAndSettings, accountsData),
                                    attributes = settingsToMap(boxAndSettings.isActive),
                                    expansionStatus = ExpansionStatus.NOT_EXPANDABLE,
                                    nodes = emptyList()
                                )
                            )
                        )
                    }
                )
            }
        )
    }

    private fun flatNodes(root: Node): List<AdminTreeItem> {
        val items = mutableListOf<AdminTreeItem>()
        val level = 0
        doFlatNodes(root, level, items)
        return items
    }

    private fun doFlatNodes(node: Node, level: Int, items: MutableList<AdminTreeItem>) {
        val item = AdminTreeItem(
            id = node.id,
            level = level,
            expansionStatus = node.expansionStatus,
            attributes = node.attributes
        )
        items.add(item)
        for (child in node.nodes) {
            doFlatNodes(child, level + 1, items)
        }
    }

    private fun getRootId(): Long = ADMIN_ACCOUNT_ID

    private fun getAccountId(accountFullData: AccountFullData): Long = accountFullData.account.id or ACCOUNT_MASK

    private fun getBoxId(boxAndSettings: BoxAndSettings): Long = boxAndSettings.box.id or BOX_MASK

    private fun getSettingsId(boxAndSettings: BoxAndSettings, accountFullData: AccountFullData): Long =
        boxAndSettings.box.id or SETTINGS_MASK or (accountFullData.account.id shl 32)

    private fun getExpansionStatus(id: Long, hasChildren: Boolean, expandedIds: Set<Long>): ExpansionStatus {
        if (!hasChildren) return ExpansionStatus.NOT_EXPANDABLE
        return if (expandedIds.contains(id)) {
            ExpansionStatus.EXPANDED
        } else ExpansionStatus.COLLAPSED
    }

    private fun accountToMap(account: Account): Map<String, String> {
        return mapOf(
            resources.getString(R.string.account_id) to account.id.toString(),
            resources.getString(R.string.account_email) to account.email,
            resources.getString(R.string.account_username) to account.username
        )
    }

    private fun boxToMap(box: Box): Map<String, String> {
        return mapOf(
            resources.getString(R.string.box_id) to box.id.toString(),
            resources.getString(R.string.box_name) to box.colorName,
            resources.getString(R.string.box_value) to String.format("#%06X", (0xFFFFFF and box.colorValue))
        )
    }

    private fun settingsToMap(isActive: Boolean): Map<String, String> {
        val isActiveString = resources.getString(if (isActive) R.string.yes else R.string.no)
        return mapOf(
            resources.getString(R.string.setting_is_active) to isActiveString
        )
    }

    private class Node(
        val id: Long,
        val attributes: Map<String, String>,
        val expansionStatus: ExpansionStatus,
        val nodes: List<Node>
    )

    private class ExpansionsState(
        val identifiers: Set<Long>
    )

    private companion object {
        const val ACCOUNT_MASK = 0x2L shl 60
        const val BOX_MASK = 0x3L shl 60
        const val SETTINGS_MASK = 0x4L shl 60

        const val ADMIN_ACCOUNT_ID = 1L
    }
}