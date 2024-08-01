package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.AllDeclaredViewModels
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration
import com.latenighthack.viewmodel.codegen.v1.toByteArray

class ProtoViewModelGenerator(
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
            "binpb"
        ).apply {
            write(AllDeclaredViewModels(viewModels).toByteArray())
        }.close()
    }
}
