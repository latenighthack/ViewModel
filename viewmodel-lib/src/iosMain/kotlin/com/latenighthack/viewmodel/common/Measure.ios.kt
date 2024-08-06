package com.latenighthack.viewmodel.common

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval

public actual class Measure {

    private val startTime: Long = currentTime()

    private fun currentTime(): Long {
        memScoped {
            val timeVal = alloc<timeval>()

            gettimeofday(timeVal.ptr, null)

            return (timeVal.tv_sec * 1000) + (timeVal.tv_usec / 1000)
        }
    }

    public actual fun stop(): Long {
        return currentTime() - startTime
    }
}