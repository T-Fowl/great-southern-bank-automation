package com.tfowl.gsb.cli

import com.github.michaelbull.result.unwrap
import com.tfowl.gsb.GSBMember

class Accounts : GsbSubcommand() {

    override fun run(member: GSBMember) {
        member.accounts().unwrap().forEach { println(it) }
    }
}