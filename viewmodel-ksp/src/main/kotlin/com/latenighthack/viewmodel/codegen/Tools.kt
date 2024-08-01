package com.latenighthack.viewmodel.codegen

import java.io.OutputStream

fun OutputStream.write(s: String) {
    write(s.encodeToByteArray())
}

fun OutputStream.writeln(s: String = "") {
    write(s.encodeToByteArray())
    write("\n")
}

fun String.toUpperCamelCase() = split("_")
    .joinToString("") {
        it[0].uppercase() + it.substring(1)
    }

fun String.toCamelCase() = split("_")
    .joinToString("") {
        it[0].uppercase() + it.substring(1)
    }
    .let {
        it[0].lowercase() + it.substring(1)
    }

fun String.toUpperSpaced() = split("_")
    .joinToString(" ") {
        it[0].uppercase() + it.substring(1)
    }

fun String.toDashed() = split("_")
    .joinToString("-") { it }
