package com.latenighthack.viewmodel.common

private const val BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
private val BASE64_INVERSE_ALPHABET = IntArray(256) { BASE64_ALPHABET.indexOf(it.toChar()) }

public fun dummyByteArrayMethod(data: ByteArray): ByteArray {
    return data
}

internal fun String.fromBase64String(): ByteArray = encodeToByteArray().decodeBase64()

internal fun ByteArray.toBase64String(): String {
    val output = mutableListOf<Char>()
    var padding = 0
    var position = 0
    while (position < this.size) {
        var b = this[position].toPositiveInt() shl 16 and 0xFFFFFF
        if (position + 1 < this.size) b = b or (this[position + 1].toPositiveInt() shl 8) else padding++
        if (position + 2 < this.size) b = b or (this[position + 2].toPositiveInt()) else padding++
        for (i in 0 until 4 - padding) {
            val c = b and 0xFC0000 shr 18
            output.add(BASE64_ALPHABET[c])
            b = b shl 6
        }
        position += 3
    }

    return output.toCharArray().concatToString()
}

internal fun ByteArray.decodeBase64(): ByteArray {
    val output = mutableListOf<Byte>()
    var position = 0
    while (position < this.size) {
        var b: Int
        if (BASE64_INVERSE_ALPHABET[this[position].toPositiveInt()] != -1) {
            b = BASE64_INVERSE_ALPHABET[this[position].toPositiveInt()] and 0xFF shl 18
        } else {
            position++
            continue
        }
        var count = 0
        if (position + 1 < this.size && BASE64_INVERSE_ALPHABET[this[position + 1].toPositiveInt()] != -1) {
            b = b or (BASE64_INVERSE_ALPHABET[this[position + 1].toPositiveInt()] and 0xFF shl 12)
            count++
        }
        if (position + 2 < this.size && BASE64_INVERSE_ALPHABET[this[position + 2].toPositiveInt()] != -1) {
            b = b or (BASE64_INVERSE_ALPHABET[this[position + 2].toPositiveInt()] and 0xFF shl 6)
            count++
        }
        if (position + 3 < this.size && BASE64_INVERSE_ALPHABET[this[position + 3].toPositiveInt()] != -1) {
            b = b or (BASE64_INVERSE_ALPHABET[this[position + 3].toPositiveInt()] and 0xFF)
            count++
        }
        while (count > 0) {
            val c = b and 0xFF0000 shr 16
            output.add(c.toByte())
            b = b shl 8
            count--
        }
        position += 4
    }
    return output.toByteArray()
}

private fun Byte.toPositiveInt() = toInt() and 0xFF
