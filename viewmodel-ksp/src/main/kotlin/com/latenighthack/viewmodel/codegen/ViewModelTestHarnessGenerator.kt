package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

class ViewModelTestHarnessGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val isJs: Boolean,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) {
    val resolverClassName: String get() = options.get("ViewModel_ResolverClassName") ?: "com.latenighthack.viewmodel.Core"
    val navigatorClassName: String get() = options.get("ViewModel_NavigatorClassName") ?: "com.latenighthack.viewmodel.Navigator"

    fun generate(viewModels: List<ViewModelDeclaration>) {
        val allVms = viewModels
            .map {
                it.declaration!!.qualifiedName to it
            }
            .toMap()

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.test.navigator",
            "TestClientNavigator",
            "kt"
        ).apply {
            val navigations = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else if (vm.navigationResponseType != null) {
                        """
                        |    override suspend fun navigateTo(${vm.navigationMethodName}: ${vm.argsType?.type?.qualifiedName}): ${vm.navigationResponseType.qualifiedName} {
                        |        return reportNavigationCallback<${vm.navigationResponseType.qualifiedName}>(${vm.navigationMethodName})
                        |    }
                        """.trimMargin()
                    } else {
                        """
                        |    override fun navigateTo(${vm.navigationMethodName}: ${vm.argsType?.type?.qualifiedName}) {
                        |        reportNavigation(${vm.navigationMethodName})
                        |    }
                        """.trimMargin()
                    }
                }
                .joinToString("\n\n")

            writeln(
                """
                |package com.latenighthack.viewmodel.test.navigator
                |
                |import ${navigatorClassName}
                |
                |abstract class TestClientNavigator : ${navigatorClassName}, TestNavigator {
                |$navigations
                |}
                """.trimMargin()
            )
        }

        val navigatorPackage = navigatorClassName.split('.').dropLast(1).joinToString(".")
        val viewModelPackage = navigatorPackage.split('.').dropLast(1).joinToString(".")

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.proxy",
            "ViewModelModuleCreate",
            "kt"
        ).apply {
            val createExtensions = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else {
                        """
                        |    fun KClass<${vm.declaration!!.qualifiedName}>.create(
                        |       core: ${resolverClassName},
                        |       navigatorModule: $navigatorPackage.NavigatorModule,
                        |       args: ${vm.argsType?.type?.qualifiedName},
                        |       ${if (vm.navigationResponseType != null) { "callback: suspend (${vm.navigationResponseType.qualifiedName}?) -> Unit" } else { "" }}
                        |    ): ${vm.declaration!!.qualifiedName} {
                        |        return create${vm.name.toUpperCamelCase()}ViewModel(core, navigatorModule, args${if (vm.navigationResponseType != null) { ", callback" } else { "" }}).viewModel
                        |    }
                        """.trimMargin()
                    }
                }
                .joinToString("\n\n")
            val createLookup = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else if (vm.navigationResponseType != null) {
                        null
                    } else {
                        """
                        |        is ${vm.argsType!!.type?.qualifiedName} -> ${vm.declaration!!.qualifiedName}::class.create(core, navigatorModule, args) as ViewModelType
                        """.trimMargin()
                    }
                }
                .joinToString("\n")
            val createCallbackLookup = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else if (vm.navigationResponseType == null) {
                        null
                    } else {
                        """
                        |        is ${vm.argsType!!.type?.qualifiedName} -> ${vm.declaration!!.qualifiedName}::class.create(core, navigatorModule, args, { v: ${vm.navigationResponseType.qualifiedName}? -> callback(v as ResponseType?); Unit }) as ViewModelType
                        """.trimMargin()
                    }
                }
                .joinToString("\n")
            writeln(
                """
                |package com.latenighthack.viewmodel.proxy
                |
                |import $navigatorClassName
                |import com.latenighthack.viewmodel.common.ViewModelReporter
                |import com.latenighthack.viewmodel.NavigableViewModel
                |import com.latenighthack.viewmodel.NavigatorArgs
                |import $navigatorPackage.NavigatorModule
                |import $viewModelPackage.*
                |import kotlin.reflect.KClass
                |
                |$createExtensions
                |
                |@Suppress("UNCHECKED_CAST")
                |public inline fun <ViewModelType: NavigableViewModel<*, ArgsType>, ArgsType : NavigatorArgs, ResponseType> createViewModelCallback(core: ${resolverClassName}, navigatorModule: $navigatorPackage.NavigatorModule, args: ArgsType, crossinline callback: suspend (ResponseType?) -> Unit): ViewModelType {
                |   return when (args) {
                |$createCallbackLookup
                |       else -> TODO()
                |   }
                |}
                |
                |@Suppress("UNCHECKED_CAST")
                |public inline fun <ViewModelType: NavigableViewModel<*, ArgsType>, ArgsType : NavigatorArgs> createViewModel(core: ${resolverClassName}, navigatorModule: $navigatorPackage.NavigatorModule, args: ArgsType): ViewModelType {
                |   return when (args) {
                |$createLookup
                |       else -> TODO()
                |   }
                |}
                """.trimMargin()
            )
        }
    }
}
