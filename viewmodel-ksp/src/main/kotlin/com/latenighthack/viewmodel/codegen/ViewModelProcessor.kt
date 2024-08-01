package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Modifier
import com.latenighthack.viewmodel.annotations.DeclareChildViewModel
import com.latenighthack.viewmodel.annotations.DeclareViewModelList
import com.latenighthack.viewmodel.codegen.v1.ActionName
import com.latenighthack.viewmodel.codegen.v1.AllDeclaredViewModels
import com.latenighthack.viewmodel.codegen.v1.ClassDeclaration
import com.latenighthack.viewmodel.codegen.v1.FunctionDeclaration
import com.latenighthack.viewmodel.codegen.v1.Type
import com.latenighthack.viewmodel.codegen.v1.ViewModelAction
import com.latenighthack.viewmodel.codegen.v1.ViewModelArgs
import com.latenighthack.viewmodel.codegen.v1.ViewModelArgsProperty
import com.latenighthack.viewmodel.codegen.v1.ViewModelChildDeclaration
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration
import com.latenighthack.viewmodel.codegen.v1.ViewModelListDeclaration
import com.latenighthack.viewmodel.codegen.v1.ViewModelMutation
import com.latenighthack.viewmodel.codegen.v1.ViewModelState
import com.latenighthack.viewmodel.codegen.v1.ViewModelStateProperty
import com.latenighthack.viewmodel.codegen.v1.fromByteArray
import java.io.FileInputStream
import kotlin.random.Random
import kotlin.random.nextUInt

fun KSClassDeclaration.toClassDeclaration() = ClassDeclaration(simpleName.asString(), qualifiedName!!.asString())
fun KSType.toType() = Type(declaration.simpleName.asString(), declaration.qualifiedName!!.asString())
fun KSFunctionDeclaration.toFunctionDeclaration() =
    FunctionDeclaration(simpleName.asString(), qualifiedName!!.asString())

fun String.camelWords(): List<String> = split(Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))

fun KSName.getActionParts(): ActionName {
    var str = this.asString()

    return str.getActionParts()
}

fun String.getActionParts(): ActionName {
    var str = this

    if (str.startsWith("on")) {
        str = str.substring(2)
    } else if (str.startsWith("is")) {
        str = str.substring(2)
    }

    val words = str.camelWords().map { it.lowercase() }

    val verb = if (words.size > 1) {
        words.last()
    } else {
        null
    }
    val noun = if (words.size > 1) {
        words.subList(0, words.size - 1).joinToString("_")
    } else {
        words.last()
    }

    return ActionName(noun, verb ?: "")
}

fun KSName.asStrippedViewModelString(): String {
    var str = this.asString()

    if (str.startsWith("I") && str[1].isUpperCase()) {
        str = str.substring(1)
    }

    if (str.endsWith("ViewModel")) {
        str = str.substring(0, str.length - 9)
    }

    val words = str.camelWords().map { it.lowercase() }

    str = words.joinToString("_")

    return str
}

class ViewModelProcessor(
    private val codeGenerator: CodeGenerator,
    private val log: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    var viewModels: List<ViewModelDeclaration>? = null
    lateinit var dependencies: Dependencies

    val navigatorClassName: String get() = options.get("ViewModel_NavigatorClassName") ?: "com.latenighthack.viewmodel.Navigator"

    override fun finish() {
        super.finish()

        codeGenerator.createNewFile(
            dependencies,
            "codegen",
            "dummy", "txt"
        )
            .close()

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.gen",
            "finish_log", "txt")
        .also { info ->
            val dummyFile = codeGenerator.generatedFile.first()
            val buildTargetFolder = dummyFile.parentFile.parentFile.parentFile // ie. androidDebug, commonMain
            val buildTargetTypeFolder = buildTargetFolder.parentFile // ie. android, metadata, etc.
            if (buildTargetTypeFolder.name == "metadata") {
                return
            }

            val projectTypeFolder = buildTargetFolder
            val projectType = projectTypeFolder.name

            val projectFolder = if (projectType.endsWith("Debug") || projectType.endsWith("Release") || projectType.endsWith("Main")) {
                buildTargetTypeFolder.parentFile.parentFile.parentFile.parentFile
            } else {
                buildTargetTypeFolder.parentFile.parentFile.parentFile
            }
            val projectName = projectFolder.name

            val jsonGenerator = JsonViewModelGenerator(dependencies, codeGenerator, log, options)
            val protoGenerator = ProtoViewModelGenerator(dependencies, codeGenerator, log, options)

            viewModels?.let { viewModels ->
                jsonGenerator.generate(viewModels)
                protoGenerator.generate(viewModels)

//                if (projectName == "viewmodel") {
                info.writeln("Writing view model reporter proxy")
                    val reporterProxyGenerator =
                        ReporterProxyGenerator(dependencies, codeGenerator, projectType.startsWith("js"), log, options)

                    reporterProxyGenerator.generate(viewModels)
//                }

                if (projectType.startsWith("js")) {
                    info.writeln("Writing JS Vue Proxies")
                    val vueProxyGenerator = VueRefProxyGenerator(dependencies, codeGenerator, log, options)
                    val vueScreenComponentGenerator = VueScreenComponentGenerator(dependencies, codeGenerator, log, options)

                    vueProxyGenerator.generate(viewModels)
                    vueScreenComponentGenerator.generate(viewModels)
                }

                if (projectName.startsWith("android")) {
                    info.writeln("Writing Android activities")
                    val modelInputStream = FileInputStream(
                        projectFolder.parentFile.resolve(
                            "viewmodel/build/generated/ksp/android" +
                                    "/android${projectType.toUpperCamelCase()}" +
                                    "/resources/com/latenighthack/viewmodel/gen/models.binpb"
                        )
                    )

                    val viewModels = AllDeclaredViewModels.fromByteArray(modelInputStream.readAllBytes()).models

                    val androidActivityGenerator = AndroidActivityGenerator(dependencies, codeGenerator, log, options)

                    androidActivityGenerator.generate(viewModels)
                }
            }

            info.writeln("target=${buildTargetFolder.name}")
            info.writeln("target_type=${buildTargetTypeFolder.name}")
            info.writeln("project_folder=${projectTypeFolder}")
            info.writeln("project_name=${projectName}")
            info.writeln("navigator_class=${navigatorClassName}")
            info.writeln("gen_file_path=${codeGenerator.generatedFile
                .first().toString()}")
        }
        .close()
    }

    private var first = true

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!first) {
            return emptyList()
        }

        first = false
        
        val symbols = resolver
            .getSymbolsWithAnnotation("com.latenighthack.viewmodel.annotations.DeclareViewModel")
            .filterIsInstance<KSClassDeclaration>()

        this.dependencies = Dependencies(true, *symbols.mapNotNull { it.containingFile }.toList().toTypedArray())

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.gen",
            "process_log_${Random.nextUInt().toString(16)}", "txt").also { info ->

            val navigatorClass = resolver.getKotlinClassByName(navigatorClassName) ?: return emptyList()

            val navigatorMapping = navigatorClass!!.getAllFunctions()
                .mapNotNull {
                    if (it.simpleName.asString() != "navigateTo") {
                        return@mapNotNull null
                    } else {
                        val param = it.parameters.firstOrNull()

                        if (param == null) {
                            return@mapNotNull null
                        } else {
                            val paramTypeName = param.type.resolve().declaration.qualifiedName!!.asString()

                            Pair(paramTypeName, param.name?.asString())
                        }
                    }
                }
                .toMap()

            val discoveredViewModelTypes = symbols.toMutableList()
            val visitedTypes = mutableSetOf<String>()
            val viewModels = mutableListOf<ViewModelDeclaration>()

            while (discoveredViewModelTypes.size > 0) {
                val toVisit = discoveredViewModelTypes.toList()

                discoveredViewModelTypes.clear()

                viewModels.addAll(toVisit
                    .mapNotNull { vm ->
                        val name = vm.qualifiedName!!.asString()

                        if (visitedTypes.contains(name)) {
                            return@mapNotNull null
                        }

                        visitedTypes.add(name)

                        val vmTypeArgs = vm.getAllSuperTypes().toList()
                            .first {
                                val typeName = it.declaration.qualifiedName!!.asString()

                                typeName == "com.latenighthack.viewmodel.NavigableViewModel" ||
                                        typeName == "com.latenighthack.viewmodel.ViewModel"
                            }
                            .let {
                                it.arguments
                                    .toList()
                                    .map {
                                        it.type!!.resolve().declaration
                                    }
                            }

                        val isNavigable = vm.getAllSuperTypes()
                            .find { it.declaration.qualifiedName!!.asString() == "com.latenighthack.viewmodel.NavigableViewModel" } != null
                        val actionSuperType = vm.getAllSuperTypes()
                            .find { it.declaration.qualifiedName!!.asString() == "com.latenighthack.viewmodel.IActionItemViewModel" }
                        val asyncActionSuperType = vm.getAllSuperTypes()
                            .find { it.declaration.qualifiedName!!.asString() == "com.latenighthack.viewmodel.IAsyncActionItemViewModel" }
                        val headerSuperType = vm.getAllSuperTypes()
                            .find { it.declaration.qualifiedName!!.asString() == "com.latenighthack.viewmodel.IHeaderItemViewModel" }
                        val dummySuperType = vm.getAllSuperTypes()
                            .find { it.declaration.qualifiedName!!.asString() == "com.latenighthack.viewmodel.IDummyItemViewModel" }

                        val stateType = if (vmTypeArgs[0] is KSClassDeclaration) {
                            vmTypeArgs[0] as KSClassDeclaration
                        } else {
                            (vmTypeArgs[0] as KSTypeParameter).bounds.first().resolve().declaration as KSClassDeclaration
                        }
                        val argsType = if (vmTypeArgs.size > 1) {
                            vmTypeArgs[1] as KSClassDeclaration
                        } else {
                            null
                        }
                        val navigatorMethodName = argsType?.qualifiedName?.asString()?.let {
                            navigatorMapping[it]
                        } ?: ""

                        val actionMethods = vm.getDeclaredFunctions()
                            .mapNotNull { method ->
                                if (method.annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == "com.latenighthack.viewmodel.annotations.CodegenIgnore" } != null) {
                                    null
                                } else if (method.simpleName.asString() == "<init>") {
                                    null
                                } else if (!method.isPublic()) {
                                    null
                                } else if (method.parameters.isEmpty()) {
                                    method
                                } else {
                                    null
                                }
                            }
                            .toMutableList()

                        if (actionSuperType != null) {
                            val actionMethodFilter = listOf("onItemTapped")
                            for (am in actionMethodFilter) {
                                if (actionMethods.firstOrNull { it.simpleName.asString() == am } == null) {
                                    val method = (actionSuperType.declaration as KSClassDeclaration).getDeclaredFunctions()
                                        .firstOrNull { it.simpleName.asString() == am }

                                    if (method != null) {
                                        actionMethods.add(method)
                                    }
                                }
                            }
                        }
                        if (asyncActionSuperType != null) {
                            val actionMethodFilter = listOf("onItemTapped")
                            for (am in actionMethodFilter) {
                                if (actionMethods.firstOrNull { it.simpleName.asString() == am } == null) {
                                    val method =
                                        (asyncActionSuperType.declaration as KSClassDeclaration).getDeclaredFunctions()
                                            .firstOrNull { it.simpleName.asString() == am }

                                    if (method != null) {
                                        actionMethods.add(method)
                                    }
                                }
                            }
                        }

                        val viewModelName = vm.simpleName.asStrippedViewModelString()

                        val vmState = ViewModelState(
                            stateType.toClassDeclaration(),
                            stateType.getDeclaredProperties().map { prop ->
                                ViewModelStateProperty(prop.simpleName.asString(), prop.type.resolve().toType())
                            }.toList()
                        )

                        val actions = actionMethods
                            .map { method ->
                                val navigations = (method.annotations.toList()
                                    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() != "kotlin.Throws" }
                                    .firstOrNull()?.arguments?.first()?.value as? java.util.ArrayList<*>)
                                    ?.map {
                                        (it as KSType).declaration as KSClassDeclaration
                                    }
                                    ?.toList() ?: emptyList()

                                // todo: this only resolves the current classes throws, not super types.
                                //
                                // This means that we lose Throws() information in subclasses, unless it's a _direct_ descendant
                                // of I(Async)ActionViewModel. If we want to correctly catch ViewModelException in all cases,
                                // we need to walk up the chain recursively to find a base type that throws it.
                                val throws = (method.annotations.toList()
                                    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == "kotlin.Throws" }
                                    .firstOrNull()?.arguments?.first()?.value as? java.util.ArrayList<*>)
                                    ?.map { (it as KSType).declaration as KSClassDeclaration }
                                    ?.mapNotNull { it.qualifiedName?.asString() }
                                    ?.toList() ?: emptyList()

                                ViewModelAction(
                                    method.simpleName.getActionParts(),
                                    method.simpleName.asString(),
                                    method.toFunctionDeclaration(),
                                    method.modifiers.contains(Modifier.SUSPEND),
                                    throws,
                                    navigations.map { it.toClassDeclaration() }
                                )
                            }
                        val mutators = vm.getDeclaredFunctions()
                            .mapNotNull { method ->
                                val navigations = (method.annotations.toList()
                                    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() != "kotlin.Throws" }
                                    .firstOrNull()?.arguments?.first()?.value as? java.util.ArrayList<*>)
                                    ?.map {
                                        (it as KSType).declaration as KSClassDeclaration
                                    }
                                    ?.toList() ?: emptyList()

                                if (method.annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == "com.latenighthack.viewmodel.annotations.CodegenIgnore" } != null) {
                                    null
                                } else if (method.simpleName.asString() == "<init>") {
                                    null
                                } else if (!method.isPublic()) {
                                    null
                                } else if (method.parameters.size == 1) {
                                    ViewModelMutation(
                                        method.simpleName.getActionParts(),
                                        method.simpleName.asString(),
                                        method.parameters[0].type.resolve().toType(),
                                        method.parameters[0].toString(),
                                        method.modifiers.contains(Modifier.SUSPEND),
                                        method.toFunctionDeclaration(),
                                        navigations.map { it.toClassDeclaration() }
                                    )
                                } else {
                                    null
                                }
                            }
                            .toList()
                        val lists = vm.getDeclaredProperties()
                            .mapNotNull { property ->
                                property.getAnnotationsByType(DeclareViewModelList::class)
                                    .toList()
                                    .firstOrNull()
                                    ?.let { _ ->
                                        val itemTypes = (property.annotations.toList()
                                            .first().arguments.first().value as java.util.ArrayList<*>)
                                            .map {
                                                (it as KSType).declaration as KSClassDeclaration
                                            }
                                            .toList()

                                        for (itemType in itemTypes) {
                                            discoveredViewModelTypes.add(itemType)
                                        }

                                        val t = property.type.resolve()
                                        if (t.arguments.isEmpty()) {
                                            throw Exception("invalid thing here")
                                        }
                                        if (t.arguments[0].type!!.resolve().arguments.isEmpty()) {
                                            throw Exception("invalid thing here")
                                        }

                                        ViewModelListDeclaration(
                                            property.simpleName.asString(),
                                            property.type.resolve().toType(),
                                            itemTypes.map { it.toClassDeclaration() },
                                            property.type.resolve().arguments[0].type!!.resolve().arguments[0].type!!.resolve().declaration.qualifiedName!!.asString()
                                        )
                                    }
                            }
                            .toList()

                        val ignoredChildren = vm.getDeclaredProperties()
                            .mapNotNull { property ->
                                val isDeclaredChild = property.getAnnotationsByType(DeclareChildViewModel::class)
                                    .toList()
                                    .firstOrNull() != null
                                val isDeclaredChildList = property.getAnnotationsByType(DeclareViewModelList::class)
                                    .toList()
                                    .firstOrNull() != null

                                if (isDeclaredChildList || isDeclaredChild) {
                                    null
                                } else {
                                    val type = property.type.resolve().declaration as KSClassDeclaration

                                    ViewModelChildDeclaration(
                                        property.simpleName.asString(),
                                        type.toClassDeclaration()
                                    )
                                }
                            }
                            .toList()
                        val children = vm.getDeclaredProperties()
                            .mapNotNull { property ->
                                property.getAnnotationsByType(DeclareChildViewModel::class)
                                    .toList()
                                    .firstOrNull()
                                    ?.let { _ ->
                                        val type = property.type.resolve().declaration as KSClassDeclaration

                                        discoveredViewModelTypes.add(type)

                                        ViewModelChildDeclaration(
                                            property.simpleName.asString(),
                                            type.toClassDeclaration()
                                        )
                                    }
                            }
                            .toList()

                        val args = argsType?.let {
                            ViewModelArgs(
                                it.toClassDeclaration(),
                                it.getDeclaredProperties().map { prop ->
                                    ViewModelArgsProperty(
                                        prop.simpleName.asString(),
                                        prop.type.resolve().toType(),
                                        prop.annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == "com.latenighthack.viewmodel.annotations.DeclareRouteArg" } != null)
                                }.toList()
                            )
                        }

                        val webPath =
                            vm.annotations.firstOrNull()?.arguments?.firstOrNull { it.name?.asString() == "webPath" }?.value as? String

                        ViewModelDeclaration(
                            viewModelName,
                            isNavigable,
                            vm.toClassDeclaration(),
                            vmState,
                            args,
                            actions,
                            mutators,
                            lists,
                            children,
                            ignoredChildren,
                            navigatorMethodName,
                            webPath ?: ""
                        )
                    })
            }

            this.viewModels = viewModels.toList()
        }.close()

        return emptyList()
    }
}
