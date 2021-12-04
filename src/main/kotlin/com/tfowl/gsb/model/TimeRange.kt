package com.tfowl.gsb.model

import java.time.LocalDate

sealed class TimeRange {
    object Last6Months : TimeRange()
    data class Custom(val from: LocalDate? = null, val to: LocalDate? = null) : TimeRange()
}
