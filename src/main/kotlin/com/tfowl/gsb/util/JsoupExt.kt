package com.tfowl.gsb.impl

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toErrorIf
import com.github.michaelbull.result.toResultOr
import com.tfowl.gsb.GSBError
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

internal fun Element.selectFirstAsResult(selector: String): Result<Element, GSBError.ScrapingError> =
    selectFirst(selector).toResultOr { GSBError.ScrapingError("Could not find element with selector: $selector") }

internal fun Element.selectSomeAsResult(selector: String): Result<Elements, GSBError.ScrapingError> =
    Ok(select(selector)).toErrorIf({ it.isEmpty() }) { GSBError.ScrapingError("Could not find elements with selector: $selector") }