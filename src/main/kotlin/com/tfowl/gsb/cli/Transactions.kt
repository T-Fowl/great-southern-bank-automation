package com.tfowl.gsb.cli

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.defaultStdout
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.outputStream
import com.github.michaelbull.result.unwrap
import com.jakewharton.picnic.Table
import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table
import com.tfowl.gsb.GSBMember
import com.tfowl.gsb.model.AccountNumber
import com.tfowl.gsb.model.Transaction
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.io.html
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import org.jetbrains.kotlinx.dataframe.io.writeJson

enum class TransactionsOutputFormat {
    Csv,
    Json,
    Html,
    Table
}

private fun DataFrame<Transaction>.toTable(): Table = table {
    header {
        row {
            columnNames().forEach { cell(it) }
        }
    }
    body {
        forEach { dr ->
            row {
                dr.values().forEach { cell(it) }
            }
        }
    }
}

class Transactions : GsbSubcommand() {

    private val account by option().required()

    private val output by option().outputStream().defaultStdout()
    private val format by option().enum<TransactionsOutputFormat>().default(TransactionsOutputFormat.Csv)

    override fun run(member: GSBMember) {
        val transactions = member.transactions(AccountNumber(account)).unwrap()

        output.bufferedWriter().use { writer ->
            when (format) {
                TransactionsOutputFormat.Csv   -> transactions.writeCSV(writer)
                TransactionsOutputFormat.Json  -> transactions.writeJson(writer)
                TransactionsOutputFormat.Html  -> writer.append(transactions.html())
                TransactionsOutputFormat.Table -> writer.append(transactions.toTable().renderText())
            }
        }
    }
}