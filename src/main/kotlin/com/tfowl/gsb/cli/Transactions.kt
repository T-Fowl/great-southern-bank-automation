package com.tfowl.gsb.cli

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.defaultStdout
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.outputStream
import com.github.michaelbull.result.unwrap
import com.tfowl.gsb.GSBMember
import com.tfowl.gsb.model.AccountNumber
import com.tfowl.gsb.model.Transaction

enum class TransactionsOutputFormat {
    Csv,
    Json,
//    Html,
//    Table
}

private fun Transaction.toJson(): String =
    """{"date": "$date", description: "$description", balance: "$balance", debit: "$debit", credit: "$credit"}"""

private fun Transaction.toCsv(): String =
    """$date, "$description", $balance, $debit, $credit"""

//private fun DataFrame<TransactionSchema>.toTable(): Table = table {
//    header {
//        row {
//            columnNames().forEach { cell(it) }
//        }
//    }
//    body {
//        forEach { dr ->
//            row {
//                dr.values().forEach { cell(it) }
//            }
//        }
//    }
//}

class Transactions : GsbSubcommand() {

    private val account by option().required()

    private val output by option().outputStream().defaultStdout()
    private val format by option().enum<TransactionsOutputFormat>().default(TransactionsOutputFormat.Csv)

    override fun run(member: GSBMember) {
        val transactions = member.transactions(AccountNumber(account)).unwrap()

        output.bufferedWriter().use { writer ->
            when (format) {
                TransactionsOutputFormat.Csv  -> transactions.forEach { t -> writer.appendLine(t.toCsv()) }
                TransactionsOutputFormat.Json -> transactions.forEach { t -> writer.appendLine(t.toJson()) }
            }
        }
    }
}