package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

class AndroidActivityGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val log: KSPLogger,
    private val options: Map<String, String>
) {
    fun writeViewModelDeclare(vm: ViewModelDeclaration): String {
        val actionViewDeclare = vm.actions.joinToString("\n") {
            "    lateinit var ${it.name!!.noun.toCamelCase()}View: Button"
        }
        val stateViewDeclare = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> "    lateinit var ${it.name}View: TextView"
                "Boolean" -> if (it.name.endsWith("Checked")) {
                    "    lateinit var ${it.name.getActionParts().noun.toCamelCase()}View: TextView"
                } else {
                    "    // todo: ${it.name}"
                }

                else -> "    // todo: ${it.name}"
            }
        }
        val mutatorViewDeclare = vm.mutations.joinToString("\n") {
            when (it.parameterType!!.simpleName) {
                "String" -> """    lateinit var ${it.name!!.noun.toCamelCase()}View: StandardEditText"""
                else -> "    // todo: ${it.name}"
            }
        }

        return """
        |$stateViewDeclare
        |$mutatorViewDeclare
        |$actionViewDeclare
        """.trimMargin()
    }

    fun allNouns(vm: ViewModelDeclaration) = vm.actions.mapNotNull { it.name?.noun }.toSet()
        .plus(vm.mutations.mapNotNull { it.name?.noun }.toSet())

    fun nonEditStateProperties(vm: ViewModelDeclaration) = vm.state!!.properties.filter { stateProperty ->
        allNouns(vm).firstOrNull {
            stateProperty.name.startsWith(it.toCamelCase())
        } == null
    }

    fun writeViewModelCreate(
        allVms: Map<String, ViewModelDeclaration>,
        vm: ViewModelDeclaration,
        key: String = "viewModel"
    ): String {
        val actionViewCreate = vm.actions.joinToString("\n") {
            """        ${it.name!!.noun.toCamelCase()}View = Button(text = "${it.name.noun.toUpperSpaced()}").onClick${if (it.isSuspend) "(bindingScope, " else "("}${key}::${it.methodName})"""
        }

        val stateViewCreate = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> """        ${it.name}View = TextView(text = "${it.name}")"""
                else -> "        // todo: ${it.name}"
            }
        }
        val mutatorViewCreate = vm.mutations.joinToString("\n") {
            when (it.parameterType!!.simpleName) {
                "String" -> if (it.isSuspend) {
                    """        ${it.name!!.noun.toCamelCase()}View = StandardEditText("${it.name.noun.toUpperSpaced()}") {
                    |        editText.onChanged(bindingScope, ${key}::${it.methodName})
                    |}""".trimMargin()
                } else {
                    """        ${it.name!!.noun.toCamelCase()}View = StandardEditText("${it.name.noun.toUpperSpaced()}") {
                    |        editText.onChanged(${key}::${it.methodName})
                    |}""".trimMargin()
                }

                else -> "        // todo: ${it.name}"
            }
        }

        val lists = vm.lists.joinToString("\n\n") { it ->
            val typeWrappers = it.allowableTypes
                .map {
                    allVms[it.qualifiedName]!!
                }
                .joinToString("\n") {
                    /*when (it.style) {
                        is ViewModelStyle.DUMMY_ITEM ->
                            """
                                |            layoutEmpty<${it.declaration!!.qualifiedName}>("${it.name.toUpperSpaced()}")
                                """.trimIndent()

                        is ViewModelStyle.HEADER_ITEM ->
                            """
                                |            layoutHeader<${it.declaration!!.qualifiedName}>("${it.name.toUpperSpaced()}")
                                """.trimIndent()

                        is ViewModelStyle.ACTION_ITEM ->
                            """
                                |            layoutAction<${it.declaration!!.qualifiedName}>("${it.name.toUpperSpaced()}")
                                """.trimIndent()

                        else -> {*/
                            /*
                            val itemProperties = it.state!!.properties.joinToString("\n") {
                                when (it.type!!.simpleName) {
                                    "String" -> "        ${it.name}View.text = state.${it.name}"
                                    "Boolean" -> if (it.name.endsWith("Enabled")) {
                                        "        ${it.name.getActionParts().noun.toCamelCase()}View.isEnabled = state.${it.name}"
                                    } else if (it.name.endsWith("Checked")) {
                                        "        ${it.name.getActionParts().noun.toCamelCase()}View.setChecked(state.${it.name})"
                                    } else {
                                        "        // todo: ${it.name}"
                                    }
                                    else -> "        // todo: ${it.name}"
                                }
                            }
                             */

                            val onClick = it.actions.firstOrNull()?.let { action ->
                                val baseCall = if (action.isSuspend) {
                                    "onClick(bindingScope, "
                                } else {
                                    "onClick("
                                }

                                "${baseCall}vm::${action.methodName})"
                            } ?: ""

                            val textUpdater = (it.state?.properties?.firstOrNull { prop ->
                                prop.type!!.simpleName == "String"
                            } ?: it.state?.properties?.firstOrNull { prop ->
                                prop.type!!.simpleName == "Integer"
                            } ?: it.state?.properties?.firstOrNull { prop ->
                                prop.type!!.simpleName == "Boolean"
                            })?.let { prop ->
                                "text = \"\${state.${prop.name}}\""
                            } ?: ""

                            """
                                |            layout<TextView, ${it.declaration!!.qualifiedName}, ${it.state?.type!!.qualifiedName}> {
                                |                onBindView { vm ->
                                |                    text = "${it.name.toUpperSpaced()}"
                                |                    $onClick
                                |                }
                                |                
                                |                onStateChanged { _, state ->
                                |                    $textUpdater
                                |                }
                                |            }
                                """.trimMargin()
//                        }
//                    }
                }

            """
            |        VerticalRecyclerView {
            |           items(bindingScope, ${key}.${it.propertyName}) {
            |${typeWrappers}
            |           }
            |        }
            """.trimMargin()
        }

        return """
        |        // add state
        |$stateViewCreate
        |        
        |        // add mutators
        |$mutatorViewCreate
        |        // add other comments for unsupported types
        |        
        |        // add actions
        |$actionViewCreate
        |        
        |        // add lists
        |$lists""".trimMargin()
    }

    private fun writeViewModelUpdate(vm: ViewModelDeclaration): String {
        val stateViewUpdate = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> "        ${it.name}View.text = state.${it.name}"
                "Boolean" -> if (it.name.endsWith("Enabled")) {
                    "        ${it.name.getActionParts().noun.toCamelCase()}View.isEnabled = state.${it.name}"
                } else if (it.name.endsWith("Checked")) {
                    "        ${it.name.getActionParts().noun.toCamelCase()}View.setChecked(state.${it.name})"
                } else {
                    "        // todo: ${it.name}"
                }

                else -> "        // todo: ${it.name}"
            }
        }

        return stateViewUpdate
    }

    val navigatorClassName: String get() = options.get("ViewModel_NavigatorClassName") ?: "com.latenighthack.viewmodel.Navigator"
    val resolverClassName: String get() = options.get("ViewModel_ResolverClassName") ?: "com.latenighthack.viewmodel.Core"

    fun generate(viewModels: List<ViewModelDeclaration>) {
        val allVms = viewModels
            .map {
                it.declaration!!.qualifiedName to it
            }
            .toMap()

        for (vm in viewModels) {
            // val reporterProxyClassName = "${vm.name.toUpperCamelCase()}ReporterProxy"
            val activityClassName = "${vm.name.toUpperCamelCase()}Activity"
            val vmInterfaceName = vm.declaration!!.qualifiedName

            if (!vm.isNavigable) {
                log.warn("SKIPPING: com.latenighthack.viewmodel.gen.activities.${activityClassName}.kt")
                continue
            }

            val children = if (vm.children.isNotEmpty()) {
                val childLayouts = vm.children.joinToString(",\n") {
                    val childVm = allVms[it.type!!.qualifiedName]!!

                    """        VerticalLayout {
                    |
                    |${writeViewModelDeclare(childVm)}
                    |
                    |${writeViewModelCreate(allVms, childVm, "viewModel.${it.propertyName}")}
                    |
                    |            bindChildViewModel(viewModel.${it.propertyName}) { viewModel, state ->
                    |${writeViewModelUpdate(childVm)}
                    |            }
                    |        }""".trimMargin()
                }

                """
                |        ViewPager(context).addAdapterWithViews(
                |${childLayouts}
                |        ).addTo(this)
                """.trimMargin()
            } else {
                ""
            }
            val ignoredChildren = vm.ignoredChildren.joinToString("\n\n") {
                """
                |    // todo: ${it.propertyName}
                """.trimMargin()
            }

            val navigatorPackage = navigatorClassName.split('.').dropLast(1).joinToString(".")
            val viewModelPackage = navigatorPackage.split('.').dropLast(1).joinToString(".")
            val domainPackage = viewModelPackage.split('.').dropLast(1).joinToString(".")

            codeGenerator.createNewFile(
                dependencies,
                "com.latenighthack.viewmodel.gen.activities",
                activityClassName,
                "ktx"
            ).apply {
                writeln(
                    """
                    |package $domainPackage.activities
                    |
                    |import android.content.Context
                    |import android.view.View
                    |import android.widget.*
                    |import android.widget.Button
                    |import android.widget.TextView
                    |import com.latenighthack.viewmodel.ActivitiesProvider
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.common.ViewModelReporter
                    |import com.latenighthack.viewmodel.items
                    |import com.latenighthack.viewmodel.views.*
                    |import $navigatorPackage.NavigatorModule
                    |import $viewModelPackage.*
                    |import $domainPackage.CoreBaseActivity
                    |import $domainPackage.MainAndroidNavigator
                    |import $domainPackage.R
                    |import $domainPackage.view.*
                    |import $navigatorPackage.create
                    |import $navigatorPackage.Navigator
                    |import $navigatorPackage.ViewModelModule
                    |import kotlinx.coroutines.flow.Flow
                    |
                    |class $activityClassName : CoreBaseActivity<$vmInterfaceName, $vmInterfaceName.State, $vmInterfaceName.Args>() {
                    |    override fun createViewModel(): $vmInterfaceName {
                    |        return create${vm.name.toUpperCamelCase()}ViewModel(
                    |            core,
                    |            this,
                    |            args<$vmInterfaceName.Args>()
                    |        ).viewModel
                    |    }
                    |
                    |    override val navigator: Navigator
                    |        get() = MainAndroidNavigator(this, application as ActivitiesProvider)
                    |
                    |$ignoredChildren
                    |
                    |${writeViewModelDeclare(vm)}
                    |    
                    |    override fun onBindView(viewModel: $vmInterfaceName) {
                    |        // attach child view models
                    |    }
                    |    
                    |    override fun createView(context: Context, viewModel: $vmInterfaceName) = VerticalLayout {
                    |        //Titlebar("${vm.name.toUpperSpaced()}")
                    |        
                    |${writeViewModelCreate(allVms, vm)}
                    |
                    |${children}
                    |    }
                    |    
                    |    override fun onStateChanged(viewModel: $vmInterfaceName, state: $vmInterfaceName.State) {
                    |       // state update
                    |${writeViewModelUpdate(vm)}
                    |    }
                    |}
                    """.trimMargin()
                )
            }.close()
        }
    }
}
