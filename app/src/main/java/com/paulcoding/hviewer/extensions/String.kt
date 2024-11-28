package com.paulcoding.hviewer.extensions

import java.util.Locale

fun String.toCapital() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
