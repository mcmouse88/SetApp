package com.mcmouse88.two_type_adapter.sample

import com.mcmouse88.two_type_adapter.models.Cat

sealed interface BaseListItem {

    data class HeaderItem(
        val headerId: Int,
        val fromIndex: Int,
        val toIndex: Int
    ) : BaseListItem

    data class CatItem(
        val originCat: Cat
    ) : BaseListItem {
        val id: Long get() = originCat.id
        val name: String get() = originCat.name
        val photoUrl: String get() = originCat.photoUrl
        val description: String get() = originCat.description
        val isFavorite: Boolean get() = originCat.isFavorite
    }
}