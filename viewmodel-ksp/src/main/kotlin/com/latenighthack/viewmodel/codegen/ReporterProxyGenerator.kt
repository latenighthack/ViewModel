package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

class ReporterProxyGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val isJs: Boolean,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) {
    val navigatorClassName: String get() = options.get("ViewModel_NavigatorClassName") ?: "com.latenighthack.viewmodel.Navigator"

    fun generate(viewModels: List<ViewModelDeclaration>) {
        val allVms = viewModels
            .map {
                it.declaration!!.qualifiedName to it
            }
            .toMap()

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.proxy",
            "NavigationReporter",
            "kt"
        ).apply {
            val navigations = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else {
                        try {
                            """
                        |        fun trackNavigationTo${vm.navigationMethodName.toUpperCamelCase()}(reporter: ViewModelReporter) {
                        |            reporter.trackNavigation("${vm.name}")
                        |        }
                        """.trimMargin()
                        } catch (throwable: Throwable) {
                            throw RuntimeException("Can't find navigator for ${vm.name}", throwable)
                        }
                    }
                }
                .joinToString("\n\n")

            writeln(
                """
                |package com.latenighthack.viewmodel.proxy
                |
                |import com.latenighthack.viewmodel.common.ViewModelReporter
                |
                |class NavigationReporter() {
                |    companion object {
                |$navigations
                |    }
                |}
                """.trimMargin()
            )
        }

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.proxy",
            "DelegatingNavigator",
            "kt"
        ).apply {
            val navigations = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else if (vm.navigationResponseType != null) {
                        """
                        |    override suspend fun navigateTo(${vm.navigationMethodName}: ${vm.argsType?.type?.qualifiedName}): ${vm.navigationResponseType.qualifiedName} {
                        |        return delegatedNavigator.navigateTo(${vm.navigationMethodName})
                        |    }
                        """.trimMargin()
                    } else {
                        """
                        |    override fun navigateTo(${vm.navigationMethodName}: ${vm.argsType?.type?.qualifiedName}) {
                        |        delegatedNavigator.navigateTo(${vm.navigationMethodName})
                        |    }
                        """.trimMargin()
                    }
                }
                .joinToString("\n\n")

            writeln(
                """
                |package com.latenighthack.viewmodel.proxy
                |
                |import ${navigatorClassName}
                |
                |abstract class DelegatingNavigator(private val delegatedNavigator: ${navigatorClassName}) : ${navigatorClassName} {
                |$navigations
                |}
                """.trimMargin()
            )
        }

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.proxy",
            "ReportingNavigator",
            "kt"
        ).apply {
            val navigations = viewModels
                .mapNotNull { vm ->
                    if (!vm.isNavigable) {
                        null
                    } else if (vm.navigationResponseType != null) {
                        """
                        |    override suspend fun navigateTo(${vm.navigationMethodName}: ${vm.argsType?.type?.qualifiedName}): ${vm.navigationResponseType.qualifiedName} {
                        |        NavigationReporter.trackNavigationTo${vm.navigationMethodName.toUpperCamelCase()}(reporter)
                        |        return super.navigateTo(${vm.navigationMethodName})
                        |    }
                        """.trimMargin()
                    } else {
                        """
                        |    override fun navigateTo(${vm.navigationMethodName}: ${vm.argsType?.type?.qualifiedName}) {
                        |        NavigationReporter.trackNavigationTo${vm.navigationMethodName.toUpperCamelCase()}(reporter)
                        |        super.navigateTo(${vm.navigationMethodName})
                        |    }
                        """.trimMargin()
                    }
                }
                .joinToString("\n\n")

            writeln(
                """
                |package com.latenighthack.viewmodel.proxy
                |
                |import ${navigatorClassName}
                |import com.latenighthack.viewmodel.common.ViewModelReporter
                |
                |abstract class ReportingNavigator(private val reporter: ViewModelReporter, delegatedNavigator: ${navigatorClassName}) : DelegatingNavigator(delegatedNavigator) {
                |$navigations
                |}
                """.trimMargin()
            )
        }

        for (vm in viewModels) {
            val proxyClassName = "${vm.name.toUpperCamelCase()}ReporterProxy"
            val vmInterfaceName = vm.declaration!!.qualifiedName

            val navigableScreenName = if (vm.isNavigable) {
                "private val screenName: String = \"${vm.name}\""
            } else {
                ""
            }
            val nonNavigableScreenName = if (vm.isNavigable) {
                ""
            } else {
                "private val screenName: String"
            }

            val actions = vm.actions.joinToString("\n\n") {
                val throwsStr = if (isJs) "" else {
                    if (it.throws.isEmpty()) "" else
                        "@Throws(${it.throws.map { "$it::class" }.joinToString(",")})"
                }

                if (it.isSuspend) {
                    """
                    |    $throwsStr
                    |    override suspend fun ${it.function!!.simpleName}() {
                    |        val time = Measure()
                    |        try {
                    |            original.${it.function.simpleName}()
                    |            reporter.trackAction(screenName, "${vm.name}", "${it.name!!.noun}", "${it.name.verb}", true, time.stop(), null)
                    |        } catch (t: Throwable) {
                    |            reporter.trackAction(screenName, "${vm.name}", "${it.name.noun}", "${it.name.verb}", false, time.stop(), t)
                    |            // todo: report error
                    |            throw if (t is ViewModelException) t else ViewModelException(t)
                    |        }
                    |    }
                    """.trimMargin()
                } else {
                    """
                    |    $throwsStr
                    |    override fun ${it.function!!.simpleName}() {
                    |        try {
                    |            original.${it.function.simpleName}()
                    |            reporter.trackAction(screenName, "${vm.name}", "${it.name!!.noun}", "${it.name.verb}", true, null, null)
                    |        } catch (t: Throwable) {
                    |            // todo: report error
                    |            reporter.trackAction(screenName, "${vm.name}", "${it.name.noun}", "${it.name.verb}", false, null, t)
                    |            throw if (t is ViewModelException) t else ViewModelException(t)
                    |        }
                    |    }
                    """.trimMargin()
                }
            }
            val mutators = vm.mutations.joinToString("\n\n") {
                val parameterTypeName = it.parameterType!!.qualifiedName

                if (it.isSuspend) {
                    """
                    |    override suspend fun ${it.function!!.simpleName}(${it.parameterName}: $parameterTypeName) {
                    |        original.${it.function.simpleName}(${it.parameterName})
                    |    }
                    """.trimMargin()
                } else {
                    """
                    |    override fun ${it.function!!.simpleName}(${it.parameterName}: $parameterTypeName) {
                    |        original.${it.function.simpleName}(${it.parameterName}aram)
                    |    }
                    """.trimMargin()
                }
            }
            val lists = vm.lists.joinToString("\n\n") {
//                if (it.allowableTypes.size == 1) {
//                    val vmDecl = it.allowableTypes.map { classDecl -> allVms[classDecl.qualifiedName]!! }.first()
//                    if (it.resolvedDeltaType != "kotlin.Any") {
//                        return@joinToString """
//                        |    override val ${it.propertyName}: Flow<Delta<${it.resolvedDeltaType}>> = original.${it.propertyName}.flowLazyMap { item ->
//                        |        ${vmDecl.name.toUpperCamelCase()}ReporterProxy(item, reporter, "${vm.name}")
//                        |    }
//                        """.trimMargin()
//                    }
//                }
                val typeWrappers = it.allowableTypes
                    .map { classDecl -> allVms[classDecl.qualifiedName]!! }
                    .joinToString("\n") { vmDecl ->
                        "            " +
                                "is ${vmDecl.declaration!!.qualifiedName} -> ${vmDecl.name.toUpperCamelCase()}ReporterProxy(item, reporter, \"${vm.name}\")"
                    }

                """
                |    override val ${it.propertyName}: Flow<Delta<${it.resolvedDeltaType}>> = original.${it.propertyName}.flowLazyMap { item ->
                |        when (item) {
                |${typeWrappers}
                |            else -> @Suppress("UNREACHABLE_CODE") throw TODO("unknown type")
                |        }
                |    }
                """.trimMargin()
            }
            val children = vm.children.joinToString("\n\n") {
                val childVm = allVms[it.type!!.qualifiedName]!!

                """
                |    override val ${it.propertyName} by lazy {
                |        ${childVm.name.toUpperCamelCase()}ReporterProxy(original.${it.propertyName}, reporter, "${vm.name}")
                |    }
                """.trimMargin()
            }
            val ignoredChildren = vm.ignoredChildren.joinToString("\n\n") {
                """
                |    override val ${it.propertyName} by lazy {
                |       original.${it.propertyName}
                |    }
                """.trimMargin()
            }

            codeGenerator.createNewFile(
                dependencies,
                "com.latenighthack.viewmodel.proxy",
                proxyClassName,
                "kt"
            ).apply {
                val maybeArgs = if (vm.isNavigable) {
                    "override val args = original.args"
                } else {
                    ""
                }
                writeln(
                    """
                    |package com.latenighthack.viewmodel.proxy
                    |
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.list.*
                    |import com.latenighthack.viewmodel.common.ViewModelReporter
                    |import kotlinx.coroutines.flow.Flow
                    |
                    |public class $proxyClassName(
                    |    private val original: $vmInterfaceName,
                    |    private val reporter: ViewModelReporter,
                    |    $nonNavigableScreenName
                    |) : $vmInterfaceName {
                    |
                    |    $navigableScreenName
                    |
                    |    $maybeArgs
                    |    ${if (vm.state?.type != null) "override val state = original.state" else ""}
                    |    ${if (vm.state?.type != null) "override val initialState = original.initialState" else ""}
                    |    
                    |$actions
                    |
                    |$mutators
                    |
                    |$lists
                    |
                    |$children
                    |
                    |$ignoredChildren
                    |}
                    """.trimMargin()
                )
            }.close()
        }
    }
}
