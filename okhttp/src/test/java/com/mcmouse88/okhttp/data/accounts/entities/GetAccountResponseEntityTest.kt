package com.mcmouse88.okhttp.data.accounts.entities

import com.mcmouse88.okhttp.domain.accounts.entities.Account
import org.junit.Assert
import org.junit.Test

class GetAccountResponseEntityTest {

    @Test
    fun toAccountMapsToInAppEntity() {
        val responseEntity = GetAccountResponseEntity(
            id = 3,
            email = "some-email",
            username = "some-username",
            createdAt = 123456789
        )

        val inAppEntity = responseEntity.toAccount()

        val expectedInAppEntity = Account(
            id = 3,
            email = "some-email",
            username = "some-username",
            createdAt = 123456789
        )

        Assert.assertEquals(expectedInAppEntity, inAppEntity)
    }
}