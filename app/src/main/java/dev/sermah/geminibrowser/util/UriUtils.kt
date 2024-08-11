package dev.sermah.geminibrowser.util

import java.net.URI

fun String.relativizeUri(uri: String): String =
    URI.create(this).resolve(uri).toString()
