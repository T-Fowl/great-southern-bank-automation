package com.tfowl.gsb.impl

import com.github.michaelbull.result.*
import com.microsoft.playwright.*
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import com.tfowl.gsb.BankCredentials
import com.tfowl.gsb.GSBError
import com.tfowl.gsb.GSBMember
import com.tfowl.gsb.GreatSouthernBank
import com.tfowl.gsb.model.*
import com.tfowl.gsb.util.takeIfIsNotBlank
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jsoup.Jsoup
import java.time.LocalDate
import java.util.regex.Pattern


private const val ONLINE_BANKING_URL = "https://ob.greatsouthernbank.com.au"

inline private fun <R> catch(block: () -> R): Result<R, GSBError> =
    runCatching(block).mapError { GSBError.Exception(it) }

inline private fun <T, R> T.catch(block: T.() -> R): Result<R, GSBError> =
    this.runCatching(block).mapError { GSBError.Exception(it) }

internal class GSBOnlineBankingMember(private val page: Page) : GSBMember {
    private fun Page.hasSessionEnded(): Boolean = url().endsWith("FirstLogin.action")

    private fun Page.loadSectionFrame(sectionName: String, waitForSelector: String): Result<Frame, GSBError> =
        catch {
            click("text=$sectionName")

            frame("head4").waitForSelector(waitForSelector)
            frame("head4")
        }

    private fun Page.loadSection(name: String): Frame {
        val f = frame("head4")

        f.waitForNavigation {
            page.getByRole(AriaRole.LINK, Page.GetByRoleOptions().setName(name)).click()
        }

//        page.waitForNavigation(Page.WaitForNavigationOptions().setWaitUntil(WaitUntilState.NETWORKIDLE)) {
//            page.getByRole(AriaRole.LINK, Page.GetByRoleOptions().setName(name)).click()
//        }

        return f
    }

    override fun accounts(): Result<List<Account>, GSBError> = binding {
        val frame = page.loadSection("Account Overview")
        frame.waitForSelector("#accountDash")
        val document = Jsoup.parse(frame.content())

        val table = document.selectFirstAsResult("#accountDash").bind()

        val columnNamesMapping = mapOf(
            "account" to "AccountNumber",
            "account name" to "AccountName",
            "balance" to "Balance",
            "available" to "Available"
        )

        val df = table.parseTableAsDataFrame().bind()
            .select(columnNamesMapping.keys)
            .rename { all() }.into { columnNamesMapping[it.name]!! }
            .convert("AccountNumber").with { AccountNumber(it.toString()) }
            .convert("AccountName").with { it.toString() }
            .convert("Available").with { Money.parseOrNull(it.toString()) }
            .convert("Balance").with { Money.parseOrNull(it.toString()) }
            .cast<AccountSchema>()

        df.map { it.toAccount() }
    }

    override fun statements(): Result<List<Statement>, GSBError> =
        Err(GSBError.UnsupportedOperation("Not Implemented Yet")) // TODO

    // TODO: Include pending
    override fun transactions(
        account: AccountNumber,
        timeRange: TimeRange,
    ): Result<DataFrame<TransactionSchema>, GSBError> = catch {
        val frame = page.loadSection("Transactions")

        val clickOptions = Locator.ClickOptions().setDelay(200.0)

        frame.getByRole(AriaRole.BUTTON, Frame.GetByRoleOptions().setName("Account Please Select")).click(clickOptions)

        frame.getByRole(AriaRole.OPTION, Frame.GetByRoleOptions().setName(Pattern.compile(account.number)))
            .click(clickOptions)
        frame.getByRole(AriaRole.BUTTON, Frame.GetByRoleOptions().setName("Date Range Please Select"))
            .click(clickOptions)
        frame.getByRole(AriaRole.OPTION, Frame.GetByRoleOptions().setName("The last 6 months")).click(clickOptions)
        frame.getByRole(AriaRole.BUTTON, Frame.GetByRoleOptions().setName("Search")).click(clickOptions)
        frame.getByRole(AriaRole.BUTTON, Frame.GetByRoleOptions().setName("Export")).click(clickOptions)

        val download = page.waitForDownload {
            frame.getByRole(AriaRole.MENUITEM, Frame.GetByRoleOptions().setName("CSV")).click(clickOptions)
        }

        DataFrame.readCSV(download.createReadStream(), parserOptions = ParserOptions(dateTimePattern = "d LLL uuuu"))
            .convert("Date").toLocalDate()
            .convert("Debit").with { it?.toString()?.let { Money.parseOrNull(it) } }
            .convert("Credit").with { it?.toString()?.let { Money.parseOrNull(it) } }
            .convert("Balance").with { it?.toString()?.let { Money.parseOrNull(it) } }
            .cast<TransactionSchema>()
            .update { Description }.with { it.trim() }
    }

    override fun transfer(
        from: AccountNumber,
        to: AccountNumber,
        amount: Money,
        description: TransferDescription?,
        schedule: PaymentSchedule,
    ): Result<TransferReceipt, GSBError> = Err(GSBError.UnsupportedOperation("Not Implemented Yet")) // TODO

    // TODO: Change to extracting a DataFrame
    override fun payees(location: PayeeLocation): Result<List<Payee>, GSBError> = binding {
        val frame = page.loadSection("Payee List")

        frame.waitForSelector("#addPayeeButton")
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
        from: AccountNumber,
        to: Payee,
        amount: Money,
        description: FastPaymentDescription?,
        reference: FastPaymentReference?,
        schedule: PaymentSchedule,
    ): Result<PaymentReceipt, GSBError> = Err(GSBError.UnsupportedOperation("Not Implemented Yet")) // TODO

    override fun payAnyone(
        from: AccountNumber,
        to: Payee,
        amount: Money,
        description: PaymentDescription?,
        schedule: PaymentSchedule,
    ): Result<PaymentReceipt, GSBError> = Err(GSBError.UnsupportedOperation("Not Implemented Yet")) // TODO

    override fun scheduledPayments(
        account: AccountNumber,
        time: ClosedRange<LocalDate>?,
        amount: ClosedRange<Money>?,
        type: ScheduledPaymentType,
    ): Result<List<ScheduledPayment>, GSBError> = Err(GSBError.UnsupportedOperation("Not Implemented Yet")) // TODO
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

//            page.getByPlaceholder("Customer Number").click()
            page.getByPlaceholder("Customer Number").fill(credentials.customerNumber)

//            page.getByPlaceholder("Password").click()
            page.getByPlaceholder("Password").fill(credentials.password)

            page.waitForNavigation {
                page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Login")).click()
            }

            // TODO: This will throw an exception (caught) - however we should probably try and return the correct Err type
            assertThat(page).hasURL(Pattern.compile("home.action$"))

            // eg?
//            page.content().contains("The login details you have entered are incorrect")
        }

        return Ok(GSBOnlineBankingMember(page))
    }

    override fun close() {
        playwright.close()
    }
}