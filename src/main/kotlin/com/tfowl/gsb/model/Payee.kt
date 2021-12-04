package com.tfowl.gsb.model

class Payee(
    val name: String,
    val bsb: String? = null,
    val accountNumber: String? = null,
    val payIdName: String? = null,
    val payId: String? = null,
)

enum class PayeeLocation {
    InAustralia,
    Overseas
}