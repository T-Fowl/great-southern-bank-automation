package com.tfowl.gsb.model

import com.tfowl.gsb.util.notLongerThan

@JvmInline
value class FastPaymentDescription(val value: String) {
    init {
        require(value.notLongerThan(280)) { "Fast payment description over 280 characters: $value" }
    }
}