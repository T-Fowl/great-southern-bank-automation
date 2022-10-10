package com.tfowl.gsb

import com.github.michaelbull.result.Result
import com.tfowl.gsb.model.*
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.time.LocalDate

data class BankCredentials(
    val memberNumber: String,
    val password: String,
)

interface GreatSouthernBank {
    fun login(credentials: BankCredentials): Result<GSBMember, GSBError>
}

sealed class GSBError {
    /** Unsuccessful login attempt */
    object InvalidCredentials : GSBError()

    /** Session ended - need to login again */
    object SessionEnded : GSBError()

    /** When parsing elements from the html */
    data class ScrapingError(val message: String) : GSBError()

    /** Otherwise unknown error */
    data class Exception(val throwable: Throwable) : GSBError()
}

interface GSBMember {
    fun accounts(): Result<List<Account>, GSBError>

    fun statements(): Result<List<Statement>, GSBError>

    fun transactions(
        account: AccountNumber,
        timeRange: TimeRange = TimeRange.Last6Months,
    ): Result<DataFrame<Transaction>, GSBError>

    fun transfer(
        from: AccountNumber,
        to: AccountNumber,
        amount: Money,
        description: TransferDescription? = null,
        schedule: PaymentSchedule = PaymentSchedule.Now,
    ): Result<TransferReceipt, GSBError>

    fun payees(location: PayeeLocation = PayeeLocation.InAustralia): Result<List<Payee>, GSBError>

    fun fastPayment(
        from: AccountNumber,
        to: Payee,
        amount: Money,
        description: FastPaymentDescription? = null,
        reference: FastPaymentReference? = null,
        schedule: PaymentSchedule = PaymentSchedule.Now,
    ): Result<PaymentReceipt, GSBError>

    fun payAnyone(
        from: AccountNumber,
        to: Payee,
        amount: Money,
        description: PaymentDescription? = null,
        schedule: PaymentSchedule = PaymentSchedule.Now,
    ): Result<PaymentReceipt, GSBError>

    fun scheduledPayments(
        account: AccountNumber,
        time: ClosedRange<LocalDate>? = null,
        amount: ClosedRange<Money>? = null,
        type: ScheduledPaymentType = ScheduledPaymentType.PaymentAndBills,
    ): Result<List<ScheduledPayment>, GSBError>
}