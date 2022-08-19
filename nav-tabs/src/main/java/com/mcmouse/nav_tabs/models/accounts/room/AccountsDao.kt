package com.mcmouse.nav_tabs.models.accounts.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountDbEntity
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountSignInTuple
import com.mcmouse.nav_tabs.models.accounts.room.entity.AccountUpdateUserNameTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountsDao {

    @Query("SELECT user_id, password FROM accounts WHERE email = :email")
    suspend fun findByEmail(email: String): AccountSignInTuple?

    /**
     * При обновлении записи в таблице при помощи Tuple достаточно объявить аннотацию [Update],
     * и указать в какой таблице будут обновляться данные, для этого нужно указать класс
     * который имеет аннотацию [Entity], и так как класс Tuple содержит первичный ключ и поля
     * которые будут обновляться, то система имея эти исходные данные сама обновит данные в таблице.
     */
    @Update(entity = AccountDbEntity::class)
    suspend fun updateUsername(updateUserNameTuple: AccountUpdateUserNameTuple)

    @Insert
    suspend fun createAccount(accountDbEntity: AccountDbEntity)

    @Query("SELECT * FROM accounts WHERE user_id = :accountId")
    fun getById(accountId: Long): Flow<AccountDbEntity?>
}