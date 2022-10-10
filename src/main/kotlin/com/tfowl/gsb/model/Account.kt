package com.tfowl.gsb.model

@JvmInline
value class AccountNumber(val number: String) {
    override fun toString(): String = number
}

data class Account(
    val number: AccountNumber,
    val name: String,
    val balance: Money,
    val available: Money,
)