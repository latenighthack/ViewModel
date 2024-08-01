package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

class MultiSymbolProcessor(
    private val processors: List<SymbolProcessor>
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val result = mutableListOf<KSAnnotated>()

        for (p in processors) {
            result.addAll(p.process(resolver))
        }

        return result
    }

    override fun finish() {
        super.finish()

        for (p in processors) {
            p.finish()
        }
    }

    override fun onError() {
        super.onError()

        for (p in processors) {
            p.onError()
        }
    }
}

class ViewModelProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MultiSymbolProcessor(listOf(
            ViewModelProcessor(
                environment.codeGenerator,
                environment.logger,
                environment.options
            )
        ))
    }
}
