package com.mcmouse88.multi_choice_list.presentation.list

import com.mcmouse88.multi_choice_list.domain.Cat

data class CatListItem(
    val originCat: Cat,
    val isChecked: Boolean
) {
    val id: Long get() = originCat.id
    val name: String get() = originCat.name
    val photoUrl: String get() = originCat.photoUrl
    val description: String get() = originCat.description
    val isFavorite: Boolean get() = originCat.isFavorite
}