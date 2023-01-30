package com.mcmouse88.multi_choice_list.multi_choice

import com.mcmouse88.multi_choice_list.domain.Cat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SimpleCatMultiChoiceHandler : MultiChoiceHandler<Cat>, MultiChoiceState<Cat> {

    override val totalCheckedCount: Int
        get() = checkedIds.size

    private val checkedIds = HashSet<Long>()
    private var items: List<Cat> = emptyList()
    private val stateFlow = MutableStateFlow(OnChanged())

    override fun isChecked(item: Cat): Boolean {
        return checkedIds.contains(item.id)
    }

    override fun setItemFlow(coroutineScope: CoroutineScope, itemsFlow: Flow<List<Cat>>) {
        coroutineScope.launch {
            itemsFlow.collectLatest { list ->
                items = list
                removeDeletedCats(list)
                notifyUpdate()
            }
        }
    }

    override fun listen(): Flow<MultiChoiceState<Cat>> {
        return stateFlow.map { this }
    }

    override fun toggle(item: Cat) {
        if (isChecked(item)) {
            clear(item)
        } else {
            check(item)
        }
    }

    override fun selectAll() {
        checkedIds.addAll(items.map { it.id })
        notifyUpdate()
    }

    override fun clearAll() {
        checkedIds.clear()
        notifyUpdate()
    }

    override fun check(item: Cat) {
        if (exists(item).not()) return
        checkedIds.add(item.id)
        notifyUpdate()
    }

    override fun clear(item: Cat) {
        if (exists(item).not()) return
        checkedIds.remove(item.id)
        notifyUpdate()
    }

    private fun exists(item: Cat): Boolean {
        return items.any { it.id == item.id }
    }

    private fun removeDeletedCats(cats: List<Cat>) {
        val existingIds = HashSet(cats.map { it.id })
        val iterator = checkedIds.iterator()
        while (iterator.hasNext()) {
            val id = iterator.next()
            if (existingIds.contains(id)) {
                iterator.remove()
            }
        }
    }

    private fun notifyUpdate() {
        stateFlow.value = OnChanged()
    }

    private class OnChanged
}