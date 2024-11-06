package com.tfowl.gsb.playwright

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright

sealed class BrowserController : AutoCloseable {
    abstract val playwright: Playwright

    abstract fun connect(): Browser

    override fun close() {
        playwright.close()
    }

    class RemoteConnection(
        private val getBrowserType: Playwright.() -> BrowserType,
        private val url: String,
    ) : BrowserController() {

        override val playwright: Playwright = Playwright.create(
            Playwright.CreateOptions().setEnv(
                mapOf("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD" to "true")
            )
        )

        override fun connect(): Browser {
            return playwright.getBrowserType().connect(url)
        }
    }

    class LocalLaunch(
        private val getBrowserType: Playwright.() -> BrowserType,
        private val options: BrowserType.LaunchOptions,
    ) : BrowserController() {

        override val playwright: Playwright = Playwright.create()

        override fun connect(): Browser {
            return playwright.getBrowserType().launch(options)
        }
    }
}