package com.tfowl.gsb.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

/*
    TODO: This entire file should really be generated by the dataframe gradle plugin
 */
@DataSchema
interface Transaction {
    val Balance: com.tfowl.gsb.model.Money
    val Credit: com.tfowl.gsb.model.Money?
    val Date: kotlinx.datetime.LocalDate // d LLL yyyy
    val Debit: com.tfowl.gsb.model.Money?
    val Description: kotlin.String
}

val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Transaction>.Balance: org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money> @JvmName("Transaction_Balance") get() = this["Balance"] as org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money>
val org.jetbrains.kotlinx.dataframe.DataRow<Transaction>.Balance: com.tfowl.gsb.model.Money @JvmName("Transaction_Balance") get() = this["Balance"] as com.tfowl.gsb.model.Money
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Transaction>.Credit: org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money?> @JvmName("Transaction_Credit") get() = this["Credit"] as org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money?>
val org.jetbrains.kotlinx.dataframe.DataRow<Transaction>.Credit: com.tfowl.gsb.model.Money? @JvmName("Transaction_Credit") get() = this["Credit"] as com.tfowl.gsb.model.Money?
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Transaction>.Date: org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDate> @JvmName("Transaction_Date") get() = this["Date"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDate>
val org.jetbrains.kotlinx.dataframe.DataRow<Transaction>.Date: kotlinx.datetime.LocalDate @JvmName("Transaction_Date") get() = this["Date"] as kotlinx.datetime.LocalDate
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Transaction>.Debit: org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money?> @JvmName("Transaction_Debit") get() = this["Debit"] as org.jetbrains.kotlinx.dataframe.DataColumn<com.tfowl.gsb.model.Money?>
val org.jetbrains.kotlinx.dataframe.DataRow<Transaction>.Debit: com.tfowl.gsb.model.Money? @JvmName("Transaction_Debit") get() = this["Debit"] as com.tfowl.gsb.model.Money?
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Transaction>.Description: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Transaction_Description") get() = this["Description"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
val org.jetbrains.kotlinx.dataframe.DataRow<Transaction>.Description: kotlin.String @JvmName("Transaction_Description") get() = this["Description"] as kotlin.String
