package com.tfowl.gsb.model

import com.tfowl.gsb.util.notLongerThan

@JvmInline
value class FastPaymentReference(val value: String) {
    init {
        require(value.notLongerThan(35)) { "Fast payment reference over 35 characters: $value" }
    }
}