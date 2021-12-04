package com.tfowl.gsb

import com.tfowl.gsb.util.shorterThan

@JvmInline
value class FastPaymentDescription(val value: String) {
    init {
        require(value.shorterThan(280)) { "Fast payment description over 280 characters: $value" }
    }
}