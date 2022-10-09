package com.tfowl.gsb.cli

import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.michaelbull.result.unwrap
import com.tfowl.gsb.GSBMember
import com.tfowl.gsb.model.AccountNumber

class Transactions : GsbSubcommand() {

    private val account by option().required()

    override fun run(member: GSBMember) {
        val transactions = member.transactions(AccountNumber(account)).unwrap()

        println(transactions)
    }
}