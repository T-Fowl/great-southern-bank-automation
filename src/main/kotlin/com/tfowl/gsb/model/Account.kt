package com.tfowl.gsb.model

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.insert

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

fun DataRow<AccountSchema>.toAccount(): Account = Account(
    number = AccountNumber, name = AccountName, balance = Balance, available = Available
)

// @formatter:off

@DataSchema
interface AccountSchema {
    val AccountName: kotlin.String
    val AccountNumber: com.tfowl.gsb.model.AccountNumber
    val Available: com.tfowl.gsb.model.Money
    val Balance: com.tfowl.gsb.model.Money
}

val org.jetbrains.kotlinx.dataframe.ColumnsContainer<AccountSchema>.AccountName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("AccountSchema_AccountName") get() = this["AccountName"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
val org.jetbrains.kotlinx.dataframe.DataRow<AccountSchema>.AccountName: kotlin.String @JvmName("AccountSchema_AccountName") get() = this["AccountName"] as kotlin.String
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<AccountSchema>.AccountNumber: org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.AccountNumber> @JvmName("AccountSchema_AccountNumber") get() = this["AccountNumber"] as org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.AccountNumber>
val org.jetbrains.kotlinx.dataframe.DataRow<AccountSchema>.AccountNumber: com.tfowl.gsb.model.AccountNumber @JvmName("AccountSchema_AccountNumber") get() = this["AccountNumber"] as com.tfowl.gsb.model.AccountNumber
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<AccountSchema>.Available: org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money> @JvmName("AccountSchema_Available") get() = this["Available"] as org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money>
val org.jetbrains.kotlinx.dataframe.DataRow<AccountSchema>.Available: com.tfowl.gsb.model.Money @JvmName("AccountSchema_Available") get() = this["Available"] as com.tfowl.gsb.model.Money
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<AccountSchema>.Balance: org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money> @JvmName("AccountSchema_Balance") get() = this["Balance"] as org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money>
val org.jetbrains.kotlinx.dataframe.DataRow<AccountSchema>.Balance: com.tfowl.gsb.model.Money @JvmName("AccountSchema_Balance") get() = this["Balance"] as com.tfowl.gsb.model.Money

// @formatter:on