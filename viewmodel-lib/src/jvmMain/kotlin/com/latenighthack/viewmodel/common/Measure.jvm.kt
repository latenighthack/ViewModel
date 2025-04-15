package com.latenighthack.viewmodel.common

public actual class Measure {
    private val startTime = System.currentTimeMillis()

    public actual fun stop(): Long {
        return System.currentTimeMillis() - startTime
    }
}
