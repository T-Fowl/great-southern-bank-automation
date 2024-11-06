package com.tfowl.gsb.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.michaelbull.result.unwrap
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import com.tfowl.gsb.BankCredentials
import com.tfowl.gsb.GSBMember
import com.tfowl.gsb.impl.GSBPlaywrightOnlineBanking
import com.tfowl.gsb.playwright.BrowserController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class Gsb : NoOpCliktCommand()

abstract class GsbSubcommand : CliktCommand() {
    private val username by option().prompt(hideInput = true)
    private val password by option().prompt(hideInput = true)

    private val debug by option("--debug").flag()

    private val playwrightUrl by option("--playwright-url")

    override fun run() {
        val controller = if (playwrightUrl != null) {
            val launchArgs = Json.encodeToString(buildJsonObject {
                put("headless", false)
            })
            BrowserController.RemoteConnection(
                Playwright::chromium,
                "$playwrightUrl&launch=$launchArgs"
            )
        } else {
            BrowserController.LocalLaunch(
                Playwright::chromium, BrowserType.LaunchOptions().apply {
                    if (debug) setHeadless(false)
                }
            )
        }

        GSBPlaywrightOnlineBanking(controller).use { bank ->
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