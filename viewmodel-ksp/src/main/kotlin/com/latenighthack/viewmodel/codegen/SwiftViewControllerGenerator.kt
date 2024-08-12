package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

class SwiftViewControllerGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val log: KSPLogger,
    private val options: Map<String, String>
) {
    val buttonViewClass = "UIButton"
    val textViewClass = "UILabel"
    val editTextClass = "UITextView"
    val collectionViewClass = "UICollectionView"
    val collectionViewCellBaseClass = "UICollectionViewCell"
    val baseViewControllerClass = "CoreNavigableViewController"

    fun writeViewModelDeclare(vm: ViewModelDeclaration): String {
        val actionViewDeclare = vm.actions.joinToString("\n") {
            "    private var ${it.name!!.noun.toCamelCase()}View: ${buttonViewClass}!"
        }
        val stateViewDeclare = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> "    private var ${it.name}View: ${textViewClass}!"
                "Boolean" -> if (it.name.endsWith("Checked")) {
                    "    private var ${it.name.getActionParts().noun.toCamelCase()}View: ${textViewClass}!"
                } else {
                    "    // todo: ${it.name}"
                }

                else -> "    // todo: ${it.name}"
            }
        }
        val mutatorViewDeclare = vm.mutations.joinToString("\n") {
            when (it.parameterType!!.simpleName) {
                "String" -> """    private var ${it.name!!.noun.toCamelCase()}View: $editTextClass!"""
                else -> "    // todo: ${it.name}"
            }
        }
        val listDeclare = if (vm.lists.isNotEmpty()) {
            "    private var itemView: ${collectionViewClass}!"
        } else {
            ""
        }

        return """|$stateViewDeclare
        |$mutatorViewDeclare
        |$actionViewDeclare
        |$listDeclare
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
        parentView: String,
        primaryIsVertical: Boolean
    ): String {
        val primaryStartAnchor = if (primaryIsVertical) "topAnchor" else "leadingAnchor"
        val primaryEndAnchor = if (primaryIsVertical) "bottomAnchor" else "trailingAnchor"
        val secondaryDirection = if (primaryIsVertical) "Horizontal" else "Vertical"
        val primaryDirection = if (primaryIsVertical) "Vertical" else "Horizontal"

        val actionViewCreate = vm.actions.joinToString("\n") {
            """|        self.${it.name!!.noun.toCamelCase()}View = ${buttonViewClass}()
               |            .setTitle("${it.name.noun.toUpperSpaced()}")
               |            .addTo(${parentView})
               |            .disableAutoresizingConstraints()
               |            .constrain${secondaryDirection}(to${secondaryDirection}Of: ${parentView}, useSafeArea: true)
               |            """.trimMargin()
        }

        val stateViewCreate = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> """        self.${it.name}View = ${textViewClass}()
                            |            .addTo(${parentView})
                            |            .disableAutoresizingConstraints()
                            |            .constrain${secondaryDirection}(to${secondaryDirection}Of: ${parentView}, useSafeArea: true)"""
                    .trimMargin()
                else -> "        // todo: ${it.name}"
            }
        }
        val mutatorViewCreate = vm.mutations.joinToString("\n") {
            when (it.parameterType!!.simpleName) {
                "String" -> """        self.${it.name!!.noun.toCamelCase()}View = ${editTextClass}()
                    |            .addTo(${parentView})
                    |            .disableAutoresizingConstraints()
                    |            .constrainHeight(toConstant: 44.0)
                    |            .constrain${secondaryDirection}(to${secondaryDirection}Of: ${parentView}, useSafeArea: true)
                    |""".trimMargin()

                else -> "        // todo: ${it.name}"
            }
        }

        val list = if (vm.lists.isNotEmpty()) {
            """|        let itemViewLayoutConfiguration = UICollectionLayoutListConfiguration(appearance: .plain)
            |        let itemViewLayout = UICollectionViewCompositionalLayout.list(using: itemViewLayoutConfiguration)
            |        self.itemView = ${collectionViewClass}(frame: .zero, collectionViewLayout: itemViewLayout)
            |            .addTo(${parentView})
            |            .disableAutoresizingConstraints()
            |            .constrain${secondaryDirection}(to${secondaryDirection}Of: ${parentView}, useSafeArea: true)"""
                .trimMargin()
        } else {
            ""
        }

        val allViews = vm.actions.map {
            "${it.name!!.noun.toCamelCase()}View"
        } + nonEditStateProperties(vm).mapNotNull {
            when (it.type!!.simpleName) {
                "String" -> "${it.name}View"
                else -> null
            }
        } + vm.mutations.mapNotNull {
            when (it.parameterType!!.simpleName) {
                "String" -> "${it.name!!.noun.toCamelCase()}View"
                else -> null
            }
        } + if (vm.lists.isNotEmpty()) {
            listOf("itemView")
        } else {
            emptyList()
        }

        val constraints = """
        |        UIView.constrain${primaryDirection}Stack(
        |            ofViews: [
        |${allViews.joinToString(",\n") { "|                self.$it" }}
        |            ],
        |            startAt: ${parentView}.safeAreaLayoutGuide.$primaryStartAnchor,
        |            endAt: ${parentView}.safeAreaLayoutGuide.$primaryEndAnchor
        |        )
        """.trimIndent()

        return """|$stateViewCreate
        |$mutatorViewCreate
        |$actionViewCreate
        |$list
        |
        |$constraints""".trimMargin()
    }

    fun writeViewModelBind(
        allVms: Map<String, ViewModelDeclaration>,
        vm: ViewModelDeclaration
    ): String {
        val actionViewCreate = vm.actions.joinToString("\n") {
            """|        self.${it.name!!.noun.toCamelCase()}View.onClick${if (it.isSuspend) "Async" else ""}(viewModel.${it.methodName})""".trimMargin()
        }

        val mutatorViewCreate = vm.mutations.joinToString("\n") {
            when (it.parameterType!!.simpleName) {
                "String" ->
                    """        self.${it.name!!.noun.toCamelCase()}View.onTextChanged${if (it.isSuspend) "Async" else ""}(viewModel.${it.methodName})"""
                else -> "        // todo: ${it.name}"
            }
        }

        val list = if (vm.lists.isNotEmpty()) {
            "        self.itemView.sections(inScope: self.bindingScope) {\n${
                vm.lists.joinToString("\n") { list ->
                    """
                |            Section(flow: self.viewModel.${list.propertyName}) {
                |${list.allowableTypes.map { allVms[it.qualifiedName]!! }.joinToString("\n") { type ->
                        """
                        |                Layout<${type.declaration!!.simpleName}>(
                        |                    cellType: ${type.name.toUpperCamelCase()}Cell.self,
                        |                    reuseIdentifier: ${type.name.toUpperCamelCase()}Cell.reuseIdentifier
                        |                )""".trimMargin()
                    }}
                |            }
                |            
                """.trimMargin()
                }
    }}\n"
        } else {
            ""
        }

        return """|$mutatorViewCreate
        |$actionViewCreate
        |$list""".trimMargin()
    }

    private fun writeCellClass(vm: ViewModelDeclaration, allVms: Map<String, ViewModelDeclaration>): String {
        return """
            |class ${vm.name.toUpperCamelCase()}Cell: ${collectionViewCellBaseClass}, ViewModelBoundCell {
            |    static let reuseIdentifier = "${vm.name.toUpperCamelCase()}Cell"
            |${writeViewModelDeclare(vm)}
            |    override init(frame: CGRect) {
            |        super.init(frame: frame)
            |
            |${writeViewModelCreate(allVms, vm, "self.contentView", false)}
            |    }
            |
            |    required init?(coder: NSCoder) {
            |        fatalError("init(coder:) has not been implemented")
            |    }
            |
            |    func viewModelDidChange(_ viewModel: any Viewmodel_libViewModel) {
            |        let viewModel = viewModel as! any ${vm.declaration!!.simpleName}
            |        
            |${writeViewModelBind(allVms, vm)}
            |    }
            |
            |    func viewModelStateDidChange(_ state: Any) {
            |        let state = state as! ${vm.declaration!!.simpleName}State
            |${writeViewModelUpdate(vm)}
            |    }
            |}
        """.trimMargin()
    }

    private fun writeViewModelUpdate(vm: ViewModelDeclaration): String {
        val stateViewUpdate = nonEditStateProperties(vm).joinToString("\n") {
            when (it.type!!.simpleName) {
                "String" -> "        self.${it.name}View.text = state.${it.name}"
                "Boolean" -> if (it.name.endsWith("Enabled")) {
                    "        self.${it.name.getActionParts().noun.toCamelCase()}View.isEnabled = state.${it.name}"
                } else if (it.name.endsWith("Checked")) {
                    "        self.${it.name.getActionParts().noun.toCamelCase()}View.isOn = state.${it.name}"
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

        val coreName = "Core"

        for (vm in viewModels) {
            if (!vm.isNavigable) {
                continue
            }

            val vcClassName = "${vm.name.toUpperCamelCase()}ViewController"

            val cells = vm.lists
                .flatMap {
                    it.allowableTypes
                        .map {
                            allVms[it.qualifiedName]!!
                        }
                }
                .joinToString("\n") { listVm ->
                    writeCellClass(listVm, allVms)
                }

            codeGenerator.createNewFile(
                dependencies,
                "com.latenighthack.viewmodel.gen.vc",
                vcClassName,
                "swift"
            ).apply {
                writeln(
                    """
                    |//
                    |//  ${vcClassName}.swift
                    |//
                    |//  Generated as a Late Night Hack.
                    |//
                    |
                    |import UIKit
                    |import ViewModelSupport
                    |import $coreName
                    |
                    |${cells}
                    |
                    |class ${vcClassName}: ${baseViewControllerClass}<${vm.declaration!!.simpleName}, ${vm.declaration.simpleName}State, ${vm.declaration.simpleName}Args> {
                    |${writeViewModelDeclare(vm)}
                    |    override func createViewModel() -> ${vm.declaration!!.simpleName} {
                    |        return ${vm.name.toUpperCamelCase()}ReporterProxy(original: ${vm.name.toUpperCamelCase()}ViewModel(
                    |            args: self.args,
                    |            basicStore: self.core.simpleStore,
                    |            service: self.core.dummyService
                    |        ), reporter: self.reporter)
                    |    }
                    |
                    |    override func setup() {
                    |${writeViewModelCreate(allVms, vm, "self.view", true)}
                    |    }
                    |
                    |    override func onBindView(viewModel: any ${vm.declaration.simpleName}) {
                    |${writeViewModelBind(allVms, vm)}
                    |    }
                    |    
                    |    override func onStateChanged(viewModel: any ${vm.declaration.simpleName}, state: ${vm.declaration.simpleName}State) {
                    |${writeViewModelUpdate(vm)}
                    |    }
                    |}
                    """.trimMargin()
                )
            }.close()
        }
    }
}
