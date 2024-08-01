@file:Suppress("UNCHECKED_CAST")

package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

fun <T> List<T>.writeJson(indent: String, writeln: (String) -> Unit, write: (String) -> Unit) {
    writeln("[")
    var index = 0
    for (value in this) {
        write("  $indent")
        when (value) {
            null -> {
                write("null")
            }

            is String -> {
                write("\"$value\"")
            }

            is Boolean -> {
                write(if (value) "true" else "false")
            }

            is Map<*, *> -> {
                (value as Map<String, Any>).writeJson("$indent  ", writeln, write)
            }

            is List<*> -> {
                value.writeJson("$indent  ", writeln, write)
            }

            else -> {
                write("$value")
            }
        }

        if (index < this.size - 1) {
            writeln(",")
        } else {
            writeln("")
        }
        index += 1
    }
    write("$indent]")
}

fun Map<String, Any?>.writeJson(indent: String, writeln: (String) -> Unit, write: (String) -> Unit) {
    writeln("{")
    var index = 0
    for (entry in this) {
        val key = entry.key
        val value = entry.value

        write("  $indent\"$key\": ")
        when (value) {
            null -> {
                write("null")
            }

            is String -> {
                write("\"$value\"")
            }

            is Boolean -> {
                write(if (value) "true" else "false")
            }

            is Map<*, *> -> {
                (value as Map<String, Any>).writeJson("$indent  ", writeln, write)
            }

            is List<*> -> {
                (value as List<Any>).writeJson("$indent  ", writeln, write)
            }

            else -> {
                write("$value")
            }
        }

        if (index < this.size - 1) {
            writeln(",")
        } else {
            writeln("")
        }
        index += 1
    }
    write("$indent}")
}

class JsonViewModelGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) {
    fun generate(viewModels: List<ViewModelDeclaration>) {
        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.gen",
            "models",
            "json"
        ).apply {
            writeln("[")
            viewModels.mapIndexed { index, vm ->
                if (index > 0) {
                    writeln(",")
                }
                write("  ")
                vm.toValue().writeJson("  ", ::writeln, ::write)
            }
            writeln("")
            writeln("]")
        }.close()
    }
}
