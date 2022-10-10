package com.tfowl.gsb.model

import com.tfowl.gsb.util.notLongerThan

@JvmInline
value class TransferDescription(val value: String) {
    init {
        require(value.notLongerThan(18)) { "Transfer description over 18 characters: $value" }
    }
}