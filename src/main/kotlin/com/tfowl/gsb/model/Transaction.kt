package com.tfowl.gsb.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Transaction(
    @Contextual
    @SerialName("Date")
    val date: LocalDate,
    @SerialName("Description")
    val description: String,
    @SerialName("Balance")
    val balance: String,
    @Contextual
    @SerialName("Debit")
    val debit: Money? = null,
    @Contextual
    @SerialName("Credit")
    val credit: Money? = null,
) {
    companion object
}