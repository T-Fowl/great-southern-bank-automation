package com.tfowl.gsb.model

import java.math.BigDecimal

@JvmInline
value class Money(val value: BigDecimal) : Comparable<Money> {
    constructor(value: String) : this(BigDecimal(value))

    init {
        // TODO: GSB wouldn't support fractional cents would they?
        require(Integer.max(0, value.stripTrailingZeros().scale()) <= 2) { "Too many decimal places for money: $value" }
    }

    override fun compareTo(other: Money): Int = value.compareTo(other.value)

    operator fun plus(other: Money): Money = Money(value + other.value)

}