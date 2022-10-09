package com.tfowl.gsb.model

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@JvmInline
value class Money(val value: BigDecimal) : Comparable<Money> {
    constructor(value: String) : this(BigDecimal(value))

    init {
        // TODO: GSB wouldn't support fractional cents would they?
        require(Integer.max(0, value.stripTrailingZeros().scale()) <= 2) { "Too many decimal places for money: $value" }
    }

    override fun toString(): String = "\$$value"

    override fun compareTo(other: Money): Int = value.compareTo(other.value)

    operator fun plus(other: Money): Money = Money(value + other.value)

    operator fun minus(other: Money): Money = Money(value - other.value)

    companion object {
        private val DECIMAL_FMT = DecimalFormat().also { fmt ->
            fmt.decimalFormatSymbols = DecimalFormatSymbols().also { symbols ->
                symbols.groupingSeparator = ','
                symbols.decimalSeparator = '.'
            }
            fmt.isParseBigDecimal = true
        }

        fun parseOrNull(text: String): Money? {
            if (!text.startsWith("$")) return null

            val cleanedText = text.removePrefix("$").let { t ->
                // TODO: I have yet to produce an example where an account balance is NOT in credit
                if (t.endsWith("DB")) "-$t"
                else t
            }.replace(Regex("[^0-9.,\\-+]+"), "") // Remove everything that is not number-related

            val value = DECIMAL_FMT.parse(cleanedText) as BigDecimal

            return Money(value)
        }
    }

}