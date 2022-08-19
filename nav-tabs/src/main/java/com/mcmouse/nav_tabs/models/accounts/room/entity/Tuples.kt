package com.mcmouse.nav_tabs.models.accounts.room.entity

import androidx.room.ColumnInfo

/**
 * Tuple используется для того, чтобы получить только определенную информацию из базы данных
 * (определенные колонки), а не получать сразу все колонки. Имена полей Tuple должны совпадать с
 * именами полей сущности entity, причем если поля имеют аннотацию [ColumnInfo], то в Tuple она
 * тоже должна быть.
 */
data class AccountSignInTuple(
    @ColumnInfo(name = "user_id") val userId: Long,
    val password: String
)

/**
 * Также если нам нужно обновить только одно поле в базе данных, а не всю колонку, то для этого
 * тоже используется Tuple. Если это делать без Tuple, то несмотря на обновление одного поля
 * в базе все равно будут обновляться поля во всей колонке, просто значения в нее будут записаны
 * те же что и были. Для обновления определенных полей, нам нужно обязательно также передавать
 * идентификатор (ну или primary key, если он представлен не в качестве идентификатора), а также
 * поля которые будут обновляться.
 */
data class AccountUpdateUserNameTuple(
    @ColumnInfo(name = "user_id") val userId: Long,
    val username: String
)