package com.paulcoding.hviewer

import androidx.core.net.toUri
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val url = "https://chatgpt.com/?temporary-chat=true"
        println("${url.toUri().host}")
        assertEquals(4, 2 + 2)
    }
}