package com.mcmouse88.okhttp.domain.accounts.entities

import org.junit.Assert
import org.junit.Test

class AccountTest {

    @Test
    fun newInstanceUsesUnknownCreateAtValue() {
        val account = Account(
            id = 1,
            username = "username",
            email = "email"
        )

        val createdAt = account.createdAt
        Assert.assertEquals(Account.UNKNOWN_CREATE_AT, createdAt)
    }
}