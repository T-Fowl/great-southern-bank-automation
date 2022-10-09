package com.tfowl.gsb.model

@JvmInline
value class AccountNumber(val number: String)

data class Account(
    val number: AccountNumber,
    val name: String,
    val balance: String,
    val available: String
)