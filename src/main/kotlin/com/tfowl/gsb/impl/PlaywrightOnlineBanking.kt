@file:OptIn(ExperimentalSerializationApi::class)

package com.tfowl.gsb.impl

import com.github.michaelbull.result.*
import com.microsoft.playwright.*
import com.tfowl.gsb.*
import com.tfowl.gsb.model.*
import com.tfowl.gsb.serialisation.LocalDateSerializer
import com.tfowl.gsb.serialisation.MoneySerializer
import com.tfowl.gsb.util.takeIfIsNotBlank
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ONLINE_BANKING_URL = "https://ob.greatsouthernbank.com.au"

inline private fun <R> catch(block: () -> R): Result<R, GSBError> =
    runCatching(block).mapError { GSBError.Exception(it) }

inline private fun <T, R> T.catch(block: T.() -> R): Result<R, GSBError> =
    this.runCatching(block).mapError { GSBError.Exception(it) }

internal class GSBOnlineBankingMember(private val page: Page) : GSBMember {
    private val csv = Csv {
        hasHeaderRecord = true
        serializersModule = SerializersModule {
            contextual(MoneySerializer)
            contextual(LocalDateSerializer(DateTimeFormatter.ofPattern("d LLL yyyy")))
        }
    }

    private fun Page.hasSessionEnded(): Boolean = url().endsWith("FirstLogin.action")

    private fun Page.loadSectionFrame(sectionName: String, waitForSelector: String): Result<Frame, GSBError> =
        catch {
            click("text=$sectionName")

            frame("head4").waitForSelector(waitForSelector)
            frame("head4")
        }

    override fun accounts(): Result<List<Account>, GSBError> = binding {
        val frame = page.loadSectionFrame(sectionName = "Account Overview", waitForSelector = "#accountDash").bind()
        val document = Jsoup.parse(frame.content())

        val table = document.selectFirstAsResult("#accountDash").bind()

        val rows = table.select("tbody tr")

        val accounts = rows.map { tr ->
            val number = tr.selectFirstAsResult("td:nth-child(1)").bind().text()
            val name = tr.selectFirstAsResult("td:nth-child(3)").bind().text()
            val balance = tr.selectFirstAsResult("td:nth-child(4)").bind().text()
            val available = tr.selectFirstAsResult("td:nth-child(5)").bind().text()

            Account(number.trim(), name.trim(), balance.trim(), available.trim())
        }

        accounts
    }

    override fun statements(): Result<List<Statement>, GSBError> {
        TODO("not implemented")
    }

    override fun transactions(
        account: Account,
        timeRange: TimeRange
    ): Result<List<Transaction>, GSBError> = catch {
        page.click("text=Transactions")
        page.frame("head4").click("""a[role="button"]:has-text("Please Select")""")
        page.frame("head4").click("""li[role="option"]:has-text("${account.name} ${account.number}")""")
        page.frame("head4").click("""a[role="button"]:has-text("Please Select")""")
        page.frame("head4").click("""li[role="option"]:has-text("The last 6 months")""")
        page.frame("head4").click("""input[role="button"]:has-text("Search")""")
        page.frame("head4").click("text=Export Bar-delimitedPDFQIFCSV >> span")
        val download = page.waitForDownload { page.frame("head4").click("text=CSV") }

        csv.decodeFromString<List<Transaction>>(
            download.createReadStream().reader().readText()
        ).map { it.copy(description = it.description.trim()) }
    }

    override fun transfer(
        from: Account,
        to: Account,
        amount: Money,
        description: TransferDescription?,
        schedule: PaymentSchedule
    ): Result<TransferReceipt, GSBError> = TODO()

    override fun payees(location: PayeeLocation): Result<List<Payee>, GSBError> = binding {
        val frame = page.loadSectionFrame(sectionName = "Payee List", waitForSelector = "#addPayeeButton").bind()
        val document = Jsoup.parse(frame.content())

        val table = document.selectFirstAsResult("table").bind()
        val rows = table.select("tbody tr")

        val payees = rows.map { tr ->
            val name = tr.selectFirstAsResult("td:nth-child(1)").bind().text().trim()
            val bsb = tr.selectFirstAsResult("td:nth-child(2)").bind().text().trim()
            val accountNumber = tr.selectFirstAsResult("td:nth-child(3)").bind().text().trim()
            val payIdName = tr.selectFirstAsResult("td:nth-child(4)").bind().text().trim()
            val payId = tr.selectFirstAsResult("td:nth-child(5)").bind().text().trim()

            Payee(
                name,
                bsb.takeIfIsNotBlank(),
                accountNumber.takeIfIsNotBlank(),
                payIdName.takeIfIsNotBlank(),
                payId.takeIfIsNotBlank()
            )
        }

        payees
    }

    override fun fastPayment(
        from: Account,
        to: Payee,
        amount: Money,
        description: FastPaymentDescription?,
        reference: FastPaymentReference?,
        schedule: PaymentSchedule
    ): Result<PaymentReceipt, GSBError> = TODO()

    override fun payAnyone(
        from: Account,
        to: Payee,
        amount: Money,
        description: PaymentDescription?,
        schedule: PaymentSchedule
    ): Result<PaymentReceipt, GSBError> = TODO()

    override fun scheduledPayments(
        account: Account,
        time: ClosedRange<LocalDate>?,
        amount: ClosedRange<Money>?,
        type: ScheduledPaymentType
    ): Result<List<ScheduledPayment>, GSBError> = TODO()
}

class GSBPlaywrightOnlineBanking(browserConfig: BrowserType.LaunchOptions.() -> Unit = {}) :
    GreatSouthernBank, AutoCloseable {
    private val playwright = Playwright.create()
    private val browser = playwright.chromium().launch(
        BrowserType.LaunchOptions().apply(browserConfig)
    )
    private val context = browser.newContext(
        Browser.NewContextOptions().setAcceptDownloads(true)
    )

    override fun login(credentials: BankCredentials): Result<GSBMember, GSBError> {
        val page = context.newPage()

        @Suppress("UNUSED_VARIABLE")
        val navigation = catch {
            page.navigate(ONLINE_BANKING_URL)

            page.fill("""[placeholder="Member Number"]""", credentials.memberNumber)
            page.fill("""input[name="password"]""", credentials.password)
            page.waitForNavigation { page.press("""input[name="password"]""", "Enter") }
        }

        /* TODO: Checking for failed login
        *   - Checking `navigation.url()` does not work as it points to the login page
        *   - Checking `navigation.text()` resulted in a playwright exception being thrown
        *     For now just ignore */
        return Ok(GSBOnlineBankingMember(page))
//        return navigation.toErrorIf({ nv -> "The login details you have entered are incorrect." in nv.text() }) { GSBError.InvalidCredentials }
////        return navigation.toErrorIf({ nv -> !nv.url().endsWith("home.action") }) { GSBError.InvalidCredentials }
//            .map { GreatSouthernBankPlaywright(page) }
    }

    override fun close() {
        playwright.close()
    }
}