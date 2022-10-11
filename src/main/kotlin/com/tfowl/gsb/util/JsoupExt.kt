package com.tfowl.gsb.impl

import com.github.michaelbull.result.*
import com.tfowl.gsb.GSBError
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

internal fun Element.selectFirstAsResult(selector: String): Result<Element, GSBError.ScrapingError> =
    selectFirst(selector).toResultOr { GSBError.ScrapingError("Could not find element with selector: $selector") }

internal fun Element.selectSomeAsResult(selector: String): Result<Elements, GSBError.ScrapingError> =
    Ok(select(selector)).toErrorIf({ it.isEmpty() }) { GSBError.ScrapingError("Could not find elements with selector: $selector") }

/**
 * Does not support multiple header rows
 * Does not support column-groups or nested columns
 * Does not support multiple table bodies
 */
fun Element.parseTableAsDataFrame(
    converter: (String, Element) -> Any = { _, td -> td.text().trim() },
): Result<DataFrame<*>, GSBError.ScrapingError> {
    if (!tagName().equals("table", ignoreCase = true))
        return Err(GSBError.ScrapingError("Required a <table> element, not <${tagName()}>"))

    // Does not support column-groups or nested columns
    val columns = select("thead>tr:first-of-type>th").map { it.text().trim() }

    // Does not support multiple bodies
    val values = select("tbody:first-of-type>tr>td")

    val df = dataFrameOf(columns, values)
        .convert { all() }.perRowCol { row, column -> converter(column.name(), row[column] as Element) }

    return Ok(df)
}