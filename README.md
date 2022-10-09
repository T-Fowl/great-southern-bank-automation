Great Southern Bank Automation in Kotlin
=
---

Disclaimer
---
Very much a work-in-progress that will receive attention only as required by my own personal use-cases.

Please note that it is possible this goes against the terms and conditions of great southern bank. This repository is
hosted on github primarily as an educational resource.

---

Description
---

[Great Southern Bank](https://www.greatsouthernbank.com.au/) (Formerly CUA) unfortunately does not provide an API for
accessing and managing your bank account automatically.

This is a simple API using [Playwright](https://playwright.dev/) to control GSB's online banking website for such
reasons.
---

Example
---

```kotlin
fun main() {
    // HeadFULL for development
    GSBPlaywrightOnlineBanking { setHeadless(false) }.use { bank ->
        main(bank)
    }
}

private fun main(bank: GreatSouthernBank) {
    val credentials = readCredentials()
    val gsb = bank.login(credentials).unwrap()

    val accounts = gsb.accounts().unwrap()
    printAccounts(accounts)

    val account = accounts.first()
    val transactions = gsb.transactions(account).unwrap()
    printTransactions(account, transactions)

    val payees = gsb.payees().unwrap()
    printPayees(payees)
}
```

Output (Using [picnic](https://github.com/JakeWharton/picnic) for table generation):

```text
┌───────────────────────────────────────────────────────┐
│Accounts                                               │
├──────────────┬────────────┬─────────────┬─────────────┤
│Account Number│Account Name│Balance      │Available    │
├──────────────┼────────────┼─────────────┼─────────────┤
│xxxxxxxx      │Primary     │$xx,xxx.xx CR│$xx,xxx.xx CR│
├──────────────┼────────────┼─────────────┼─────────────┤
│xxxxxxxx      │Secondary   │$xx.xxx.xx CR│$xx,xxx.xx CR│
└──────────────┴────────────┴─────────────┴─────────────┘
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│Transactions for xxxxxxxx (Primary)                                                                                      │
├──────────┬────────────────────────────────────────────────────────┬──────────────────┬────────────────────┬─────────────┤
│Date      │Description                                             │Credit            │Debit               │Balance      │
├──────────┼────────────────────────────────────────────────────────┼──────────────────┼────────────────────┼─────────────┤
│2021-01-01│From:PAYPAL AUSTRALIA REF: xxxxxxxxxxxxx  ePayment      │                  │Money(value=x.xx)   │$xx,xxx.xx CR│
├──────────┼────────────────────────────────────────────────────────┼──────────────────┼────────────────────┼─────────────┤
│2021-01-02│From:PAYPAL AUSTRALIA REF: xxxxxxxxxxxxx  ePayment      │                  │Money(value=xx.xx)  │$xx,xxx.xx CR│
├──────────┼────────────────────────────────────────────────────────┼──────────────────┼────────────────────┼─────────────┤
│2021-01-03│From:SALARY EMPLOYER REF: xxxxxxxx  ePayment            │Money(value=x.xx) │                    │$xx,xxx.xx CR│
└──────────┴────────────────────────────────────────────────────────┴──────────────────┴────────────────────┴─────────────┘
┌───────────────────────────────────────────────────────────────────────────┐
│Payees                                                                     │
├──────────────────────┬──────┬──────────────┬────────────────┬─────────────┤
│Name                  │BSB   │Account Number│PayID Name      │PayID        │
├──────────────────────┼──────┼──────────────┼────────────────┼─────────────┤
│John SMITH            │      │              │JOHN SMITH      │+61-xxxxxxxxx│
├──────────────────────┼──────┼──────────────┼────────────────┼─────────────┤
│Jane Citizen          │xxxxxx│xxxxxxxxx     │                │             │
└──────────────────────┴──────┴──────────────┴────────────────┴─────────────┘
```