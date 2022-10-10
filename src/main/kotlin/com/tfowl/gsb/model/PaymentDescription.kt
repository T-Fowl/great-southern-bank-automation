package com.tfowl.gsb.model

import com.tfowl.gsb.util.notLongerThan

@JvmInline
value class PaymentDescription(val value: String) {
    init {
        require(value.notLongerThan(18)) { "Payment description over 18 characters: $value" }
    }
}