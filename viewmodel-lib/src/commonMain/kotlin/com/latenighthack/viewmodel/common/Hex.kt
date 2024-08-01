package com.latenighthack.viewmodel.common

private val hexCharLookup = "0123456789ABCDEF".toCharArray()

public fun ByteArray.toHexString(): String {
    val blob: ByteArray = this
    val builder = StringBuilder()

    for (byte in blob) {
        val highNybble = (byte.toInt() and 0xf0) shr 4
        val lowNybble = (byte.toInt() and 0x0f)

        builder.append(hexCharLookup[highNybble])
        builder.append(hexCharLookup[lowNybble])
    }

    return builder.toString()
}

public fun String.fromHexString(): ByteArray {
    val blob: String = this.uppercase()

    fun convertChar(char: Char): Int {
        return when (char) {
            '0' -> 0
            '1' -> 1
            '2' -> 2
            '3' -> 3
            '4' -> 4
            '5' -> 5
            '6' -> 6
            '7' -> 7
            '8' -> 8
            '9' -> 9
            'A' -> 10
            'B' -> 11
            'C' -> 12
            'D' -> 13
            'E' -> 14
            'F' -> 15
            else -> TODO()
        }
    }

    val bytes = ByteArray(blob.length / 2)

    for (j in 0 until blob.length / 2) {
        val i = j * 2

        val b0 = convertChar(blob[i])
        val b1 = convertChar(blob[i + 1])

        bytes[j] = ((b0 shl 4) or b1).toByte()
    }

    return bytes
}
