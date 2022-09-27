package com.mcmouse88.remote_mediator.ui

import com.mcmouse88.remote_mediator.ui.base.OnChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface SelectionState {

    fun isChecked(id: Long): Boolean
}

class Selections : SelectionState {

    private val checkedIds = mutableSetOf<Long>()
    private val checkedIdsFlow = MutableStateFlow(OnChange(checkedIds))

    override fun isChecked(id: Long): Boolean = checkedIds.contains(id)

    fun toggle(id: Long) {
        if (checkedIds.contains(id)) {
            checkedIds.remove(id)
        } else {
            checkedIds.add(id)
        }
        checkedIdsFlow.value = OnChange(checkedIds)
    }

    fun flow(): Flow<SelectionState> = checkedIdsFlow.map { this }
}