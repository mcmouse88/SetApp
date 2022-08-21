package com.mcmouse.nav_tabs.models.accounts.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mcmouse.nav_tabs.models.accounts.entities.Account
import com.mcmouse.nav_tabs.models.accounts.entities.SignUpData
import com.mcmouse.nav_tabs.utils.security.SecurityUtils
import java.sql.Timestamp

/**
 * Аннотация [@ColumnInfo(collate = ColumnInfo.NOCASE)] означает, что данное поле будет
 * нечувствительно к регистру. Следующая строка [indices = [Index("email", unique = true)]],
 * пропианная внутри аннотации [Entity] указывает на уникальность поля email. Если же просто
 * указать индекс без уникальности, то это будет означать, что поиск по данному индексу будет
 * происходить очень быстро, а при добавлении уникальности еще и уникальным. Но стоит иметь ввиду,
 * что индексы замедляют операции вставки. Чтобы поле id автоматически генерировалось при вставке
 * в таблицу, помимо аннотации [PrimaryKey(autoGenerate = true)] при создании объекта сущности
 * базы данных в качестве id будем записывать значение 0. При миграции базы данных и добавлении
 * новых колонок в таблицу, для новых колонок нужно выставить значение по умолчание (в нашем случае
 * колонка salt), чтобы для уже имеющихся записей были установлены значения по умолчанию.
 */
@Entity(
    tableName = "accounts",
    indices = [Index("email", unique = true)]
)
data class AccountDbEntity(
    @[PrimaryKey(autoGenerate = true) ColumnInfo(name = "user_id")] val userId: Long,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val email: String,
    val username: String,
    @ColumnInfo(name = "hash") val hash: String,
    @ColumnInfo(name = "salt", defaultValue = "") val salt: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
) {
    fun toAccount(): Account = Account(
        id = userId,
        email = email,
        username = username,
        createdAt = createdAt
    )

    companion object {
        fun fromSignUpData(signUpData: SignUpData, securityUtils: SecurityUtils): AccountDbEntity {
            val salt = securityUtils.generateSalt()
            val hash = securityUtils.passwordToHash(signUpData.password, salt)
            signUpData.password.fill('*')
            signUpData.repeatPassword.fill('*')

            return AccountDbEntity(
                0,
                email = signUpData.email,
                username = signUpData.username,
                hash = securityUtils.bytesToString(hash),
                salt = securityUtils.bytesToString(salt),
                createdAt = System.currentTimeMillis()
            )
        }
    }
}