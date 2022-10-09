package com.tfowl.gsb.model

import com.tfowl.gsb.util.shorterThan

@JvmInline
value class TransferDescription(val value: String) {
    init {
        require(value.shorterThan(18)) { "Transfer description over 18 characters: $value" }
    }
}