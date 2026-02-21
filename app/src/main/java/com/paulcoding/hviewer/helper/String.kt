package com.paulcoding.hviewer.helper

import androidx.core.net.toUri

val String.host get() = this.toUri().host ?: ""