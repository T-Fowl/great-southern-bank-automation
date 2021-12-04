package com.tfowl.gsb.util

fun String.shorterThan(length: Int): Boolean {
// TODO: Allowed characters / codepoints
    return this.length <= length
}