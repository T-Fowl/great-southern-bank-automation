package com.tfowl.gsb.util

fun String.notLongerThan(length: Int): Boolean {
// TODO: Allowed characters / codepoints
    return this.length <= length
}

fun String.takeIfIsNotBlank(): String? = takeIf { isNotBlank() }