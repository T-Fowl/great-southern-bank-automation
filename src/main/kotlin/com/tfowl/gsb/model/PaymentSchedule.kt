package com.tfowl.gsb.model

import java.time.LocalDate

sealed class RecurringUntil {
    object ICancel : RecurringUntil()
    data class EndDate(val date: LocalDate) : RecurringUntil()
}

sealed class PaymentSchedule {
    object Now : PaymentSchedule()

    data class Later(val date: LocalDate) : PaymentSchedule()

    data class Recurring(
        val start: LocalDate, val frequency: Frequency,
        val until: RecurringUntil
    ) : PaymentSchedule()
}