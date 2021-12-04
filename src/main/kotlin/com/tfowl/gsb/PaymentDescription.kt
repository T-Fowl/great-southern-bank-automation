package com.tfowl.gsb

import com.tfowl.gsb.util.shorterThan

@JvmInline
value class PaymentDescription(val value: String) {
    init {
        require(value.shorterThan(18)) { "Payment description over 18 characters: $value" }
    }
}