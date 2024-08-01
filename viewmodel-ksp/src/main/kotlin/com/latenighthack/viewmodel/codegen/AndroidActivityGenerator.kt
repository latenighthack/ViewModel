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
                "String" -> "    lateinit var ${it.name}View: PrimaryTextView"
                "Boolean" -> if (it.name.endsWith("Checked")) {
                    "    lateinit var ${it.name.getActionParts().noun.toCamelCase()}View: PrimaryTextView"
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
            """        ${it.name!!.noun.toCamelCase()}View = Button(context).apply { setText("${it.name.noun.toUpperSpaced()}") }.onClick${if (it.isSuspend) "(bindingScope, " else "("}${key}::${it.methodName}).addTo(this)"""
        }

        val stateViewCreate = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> """        ${it.name}View = PrimaryTextView("${it.name}")"""
                else -> "        // todo: ${it.name}"
            }
        }
        val mutatorViewCreate = vm.mutations.joinToString("\n") {
            when (it.parameterType!!.simpleName) {
                "String" -> if (it.isSuspend) {
                    """        ${it.name!!.noun.toCamelCase()}View = StandardEditText("${it.name.noun.toUpperSpaced()}")
                    |        ${it.name.noun.toCamelCase()}View.editText.onChanged(bindingScope, ${key}::${it.methodName})
                    """.trimMargin()
                } else {
                    """        ${it.name!!.noun.toCamelCase()}View = StandardEditText("${it.name.noun.toUpperSpaced()}")
                    |        ${it.name.noun.toCamelCase()}View.editText.onChanged(${key}::${it.methodName})
                    """.trimMargin()
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
                                |            layout<PrimaryTextView, ${it.declaration!!.qualifiedName}, ${it.state?.type!!.qualifiedName}> {
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

    fun generate(viewModels: List<ViewModelDeclaration>) {
        val allVms = viewModels
            .map {
                it.declaration!!.qualifiedName to it
            }
            .toMap()

        for (vm in viewModels) {
            // val reporterProxyClassName = "${vm.name.toUpperCamelCase()}ReporterProxy"
            val activityClassName = "Abstract${vm.name.toUpperCamelCase()}Activity"
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

            codeGenerator.createNewFile(
                dependencies,
                "com.latenighthack.viewmodel.gen.activities",
                activityClassName,
                "kt"
            ).apply {
                writeln(
                    """
                    |package com.latenighthack.viewmodel.gen.activities
                    |
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.app.activities.tools.*
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.common.ViewModelReporter
                    |import kotlinx.coroutines.flow.Flow
                    |
                    |import android.content.Context
                    |import android.widget.*
                    |import android.view.View
                    |import com.latenighthack.viewmodel.app.R
                    |import com.latenighthack.viewmodel.app.view.*
                    |import com.latenighthack.viewmodel.app.view.extensions.*
                    |import com.latenighthack.viewmodel.app.activities.tools.BaseActivity
                    |import com.latenighthack.viewmodel.app.activities.tools.navigator
                    |import androidx.viewpager.widget.ViewPager
                    |import com.latenighthack.viewmodel.app.view.items
                    |import com.latenighthack.viewmodel.app.view.onChanged
                    |import com.latenighthack.viewmodel.app.view.onClick
                    |
                    |abstract class $activityClassName : BaseActivity<$vmInterfaceName, $vmInterfaceName.State, $vmInterfaceName.Args>() {
                    |    abstract override fun createViewModel(): $vmInterfaceName
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
                    |        Titlebar("${vm.name.toUpperSpaced()}")
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
