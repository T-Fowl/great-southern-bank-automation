package com.tfowl.gsb.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.michaelbull.result.unwrap
import com.tfowl.gsb.BankCredentials
import com.tfowl.gsb.GSBMember
import com.tfowl.gsb.impl.GSBPlaywrightOnlineBanking

class Gsb : NoOpCliktCommand()

abstract class GsbSubcommand : CliktCommand() {
    private val username by option().prompt(hideInput = true)
    private val password by option().prompt(hideInput = true)

    override fun run() {
        GSBPlaywrightOnlineBanking { headless = false }.use { bank ->
            val member = bank.login(BankCredentials(username, password)).unwrap()

            run(member)
        }
    }

    abstract fun run(member: GSBMember): Unit
}

fun main(args: Array<String>) {
    return Gsb().subcommands(
        Transactions(), Pay(), Transfer(), Accounts()
    ).main(args)
}