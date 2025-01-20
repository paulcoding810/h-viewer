package com.paulcoding.js

fun log(
    message: Any?,
    tag: String? = "Logger",
) {
    val border = "*".repeat(150)
    println("\n")
    println(border)
    print("\t")
    println("$tag:")
    print("\t")
    println(message)
    println(border)
    println("\n")
}

fun <T> T.alsoLog(tag: String? = "Logger"): T {
    log(this, tag)
    return this
}
