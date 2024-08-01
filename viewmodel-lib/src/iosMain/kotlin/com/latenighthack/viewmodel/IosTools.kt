package com.latenighthack.viewmodel

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.Foundation.NSData
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public fun byteArrayToData(bytes: ByteArray): NSData = memScoped {
    NSData.create(
        bytes = bytes.toCValues().getPointer(this),
        length = bytes.size.toULong()
    )
}
