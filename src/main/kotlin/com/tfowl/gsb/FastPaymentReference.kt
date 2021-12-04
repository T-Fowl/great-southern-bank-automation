package com.tfowl.gsb

import com.tfowl.gsb.util.shorterThan

@JvmInline
value class FastPaymentReference(val value: String) {
    init {
        require(value.shorterThan(35)) { "Fast payment reference over 35 characters: $value" }
    }
}