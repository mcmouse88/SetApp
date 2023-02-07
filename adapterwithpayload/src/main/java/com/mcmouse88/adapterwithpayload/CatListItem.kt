package com.mcmouse88.adapterwithpayload

import com.mcmouse88.adapterwithpayload.model.Cat

sealed class CatListItem {

    data class Header(
        val headerId: Int,
        val fromIndex: Int,
        val toIndex: Int
    ) : CatListItem()

    data class CatItem(
        val originCat: Cat
    ) : CatListItem() {
        val id: Long get() = originCat.id
        val name: String get() = originCat.name
        val photoUrl: String get() = originCat.photoUrl
        val description: String get() = originCat.description
        val isFavorite: Boolean get() = originCat.isFavorite
    }
}