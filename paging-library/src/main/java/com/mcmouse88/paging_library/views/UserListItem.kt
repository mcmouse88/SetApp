package com.mcmouse88.paging_library.views

import com.mcmouse88.paging_library.model.users.User

data class UserListItem(
    val user: User,
    val inProgress: Boolean
) {
    val id: Long get() = user.id
    val imageUrl: String get() = user.imageUrl
    val name: String get() = user.company
    val company: String get() = user.company
    val isFavorite: Boolean get() = user.isFavorite
}