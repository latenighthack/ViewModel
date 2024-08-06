package com.latenighthack.viewmodel.common

import kotlin.js.Date

public actual class Measure {
    private val startTime = Date.now().toLong()

    public actual fun stop(): Long {
        return Date.now().toLong() - startTime
    }
}