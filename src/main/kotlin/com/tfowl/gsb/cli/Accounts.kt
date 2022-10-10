package com.tfowl.gsb.cli

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.defaultStdout
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.outputStream
import com.github.michaelbull.result.unwrap
import com.tfowl.gsb.GSBMember

enum class AccountsOutputFormat {
    Csv,
    Json,
}

class Accounts : GsbSubcommand() {

    private val output by option().outputStream().defaultStdout()
    private val format by option().enum<AccountsOutputFormat>().default(AccountsOutputFormat.Csv)

    override fun run(member: GSBMember) {
        val accounts = member.accounts().unwrap()

        /* TODO: Should we just create a DataFrame here and use its serialisation capabilities
        *   Or even change GSBMember to return a DataFrame? Probably not accounts won't often require
        *   much processing */
        output.bufferedWriter().use { writer ->
            when (format) {
                AccountsOutputFormat.Csv  -> {
                    accounts.joinTo(
                        writer,
                        separator = "\n",
                        prefix = "AccountNumber,AccountName,Balance,Available\n"
                    ) { account ->
                        "${account.number},\"${account.name}\",${account.balance},${account.available}"
                    }
                }

                AccountsOutputFormat.Json -> {
                    accounts.joinTo(
                        writer,
                        separator = ",",
                        prefix = "[",
                        postfix = "]"
                    ) { account ->
                        """{"AccountNumber":"${account.number}","AccountName":"${account.name}","Balance":"${account.balance}","Available":"${account.available}"}"""
                    }
                }
            }
        }
    }
}