package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

class VueRefProxyGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) {
    val navigatorClassName: String get() = options.get("ViewModel_NavigatorClassName") ?: "com.latenighthack.viewmodel.Navigator"
    val resolverClassName: String get() = options.get("ViewModel_ResolverClassName") ?: "com.latenighthack.viewmodel.Core"

    fun generate(viewModels: List<ViewModelDeclaration>) {
        val allVms = viewModels
            .map {
                it.declaration!!.qualifiedName to it
            }
            .toMap()

        val vueModelCreatorMethods = viewModels
            .filter { it.isNavigable }
            .joinToString("\n") {
                """
                |    @JsName("${it.name.toCamelCase()}ViewModel")
                |    fun ${it.name.toCamelCase()}ViewModel(
                |        props: dynamic,
                |        resolver: ${resolverClassName},
                |        navigator: ${navigatorClassName},
                |        ref: (dynamic) -> dynamic,
                |        unmountCallback: (() -> Unit) -> Unit,
                |        extras: dynamic
                |    ): dynamic {
                |        val bindingScope = BindingScope()
                |        
                |        return internal${it.name.toUpperCamelCase()}VueModel(
                |            props,
                |            ref,
                |            unmountCallback,
                |            bindingScope,
                |            ${it.name.toUpperCamelCase()}ReporterProxy(
                |                create${it.name.toUpperCamelCase()}ViewModel(
                |                    resolver,
                |                    object : NavigatorModule { override val navigator: Navigator get() = navigator },
                |                    jsonTo${it.name.toUpperCamelCase()}Args(props),
                |                    ${if (it.navigationResponseType != null) { "{}" } else ""}
                |                ).viewModel,
                |                reporter
                |            )
                |        )
                |    }
                """.trimMargin()
            }

        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.vue",
            "ViewModelCreator",
            "kt"
        ).apply {
            writeln(
                """
                    |package com.latenighthack.viewmodel.vue
                    |
                    |import com.latenighthack.viewmodel.common.fromHexString
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.*
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.proxy.*
                    |import gg.roll.viewmodel.core.NavigatorModule
                    |import gg.roll.viewmodel.*
                    |import $navigatorClassName
                    |import kotlinx.browser.window
                    |import kotlinx.coroutines.*
                    |import kotlinx.coroutines.flow.Flow
                    |import kotlinx.coroutines.flow.collect
                    |import kotlin.js.Promise
                    |import kotlin.js.JsExport
                    |import kotlin.js.json
                    |
                    |@kotlin.js.ExperimentalJsExport
                    |@JsExport
                    |class ViewModelVueCreator(val reporter: ViewModelReporter) {
                    |
                    |${vueModelCreatorMethods}
                    |}
                    |""".trimMargin()
            )
        }.close()

        for (vm in viewModels) {
            val proxyClassName = "${vm.name.toUpperCamelCase()}VueModel"
            val vmInterfaceName = vm.declaration!!.qualifiedName

            fun stateCopy(prefix: String, targetName: String, stateObjName: String) =
                vm.state!!.properties.joinToString("\n") {
                    prefix + "${targetName}[\"${it.name}\"] = ${stateObjName}.${it.name}"
                }

            val actions = vm.actions.joinToString("\n\n") {
                if (it.isSuspend) {
                    """
                    |    vmObject["${it.function!!.simpleName}"] = {
                    |        Promise<Unit> { resolve, reject ->
                    |            bindingScope.launch {
                    |                try {
                    |                    vm.${it.function.simpleName}()
                    |
                    |                    resolve(Unit)
                    |                } catch (t: Throwable) {
                    |                    reject(t)
                    |                }
                    |            }
                    |        }
                    |    }
                    """.trimMargin()
                } else {
                    """
                    |    vmObject["${it.function!!.simpleName}"] = {
                    |        vm.${it.function.simpleName}()
                    |    }
                    """.trimMargin()
                }
            }
            val mutators = vm.mutations.joinToString("\n\n") {
                if (it.isSuspend) {
                    """
                    |    vmObject["${it.function!!.simpleName}"] = { param ->
                    |        Promise<Unit> { resolve, reject ->
                    |            bindingScope.launch {
                    |                try {
                    |                    vm.${it.function.simpleName}(param)
                    |
                    |                    resolve(Unit)
                    |                } catch (t: Throwable) {
                    |                    reject(t)
                    |                }
                    |            }
                    |        }
                    |    }
                    """.trimMargin()
                } else {
                    """
                    |    vmObject["${it.function!!.simpleName}"] = { param ->
                    |        vm.${it.function.simpleName}(param)
                    |    }
                    """.trimMargin()
                }
            }
            val listsFwd = vm.lists.joinToString("\n") {
                """
                |    vmObject.${it.propertyName} = js("ref([])")
                """.trimMargin()
            }
            val lists = vm.lists.joinToString("\n\n") {
                val allowableTypeStrings = it.allowableTypes
                    .map {
                        allVms[it.qualifiedName]!!
                    }
                    .joinToString("\n") {
                        "                    is ${it.declaration!!.qualifiedName} -> \"${it.declaration.simpleName}\""
                    }

                val allowableTypeCodes = it.allowableTypes
                    .map {
                        allVms[it.qualifiedName]!!
                    }
                    .joinToString("\n") {
                        "                    is ${it.declaration!!.qualifiedName} -> ${it.declaration.qualifiedName.hashCode()}"
                    }


                val allowableTypeProxies = it.allowableTypes
                    .map {
                        allVms[it.qualifiedName]!!
                    }
                    .joinToString("\n") {
                        "                    is ${it.declaration!!.qualifiedName} -> internal${it.name.toUpperCamelCase()}VueModel(null, ref, { jobCancel -> saveActiveJob(jobCancel) }, bindingScope, value)"
                    }

                """
                |    val active${it.propertyName}Jobs = mutableListOf<() -> Unit>()
                |    bindingScope.launch {
                |        vm.${it.propertyName}.collect { change ->
                |            for (activeJob in active${it.propertyName}Jobs) {
                |                activeJob()
                |            }
                |            active${it.propertyName}Jobs.clear()
                |
                |            val items = change.items
                |            val size = items.size
                |
                |            val getItem: (Int) -> Any? = { index ->
                |                items[index]
                |            }
                |            
                |            val saveActiveJob: (() -> Unit) -> Unit = { jobCancel ->
                |               active${it.propertyName}Jobs.add(jobCancel)
                |            }
                |            
                |            val typeCode: (Any) -> Int = { value ->
                |                when (value) {
                |${allowableTypeCodes}
                |                    else -> 0
                |                }
                |            }
                |            
                |            val proxyType: (Any) -> Any = { value ->
                |                when (value) {
                |${allowableTypeProxies}
                |                    else -> "???"
                |                }
                |            }
                |
                |            val allProxies = object {}.asDynamic()
                |
                |            js(///function getItemProxy(n) {
                |                var itemProxy = allProxies[n]; 
                |                if (!itemProxy) {
                |                    var item = getItem(n);
                |                    var type = typeCode(item);
                |
                |                    itemProxy = proxyType(item);
                |                    itemProxy.__type = type;
                |                    allProxies[n] = itemProxy;
                |                }
                |                return itemProxy;
                |            }
                |            ///)
                |
                |            vmObjectRef["${it.propertyName}"] = js(///
                |                new Proxy([], {
                |                    get: function(target, key, receiver) {
                |                        if (key === "value") {
                |                            return items;
                |                        } else if (key === Symbol.iterator) {
                |                            return function() {
                |                                var n = 0;
                |                                var done = false;
                |
                |                                return {
                |                                    next: function() {
                |                                        var itemProxy = getItemProxy(n);
                |
                |                                        n += 1;
                |
                |                                        if (n == size) {
                |                                            done = true
                |                                        }
                |
                |                                        return { value: itemProxy, done: done };
                |                                    }
                |                                };
                |                            };
                |                        } else if (key === "length") {
                |                            return size;
                |                        } else if (typeof(key) !== "symbol" && !isNaN(+key)) {
                |                            var index = +key;
                |                            var itemProxy = getItemProxy(index);
                |
                |                            return itemProxy;
                |                        } else {
                |                            return Reflect.get(target, key, receiver);
                |                        }
                |                    }
                |                });
                |            ///)
                |        }
                |    }.invokeOnCompletion {
                |        for (activeJob in active${it.propertyName}Jobs) {
                |            activeJob()
                |        }
                |        active${it.propertyName}Jobs.clear()
                |    }
                """.trimMargin().replace('/', '"')
            }
            val children = vm.children.joinToString("\n\n") {
                val childVm = allVms[it.type!!.qualifiedName]!!

                """
                |    vmObject["${it.propertyName}"] = internal${childVm.name.toUpperCamelCase()}VueModel(null, ref, unmountCallback, bindingScope, vm.${it.propertyName});
                """.trimMargin()
            }
            val ignoredChildren = vm.ignoredChildren.joinToString("\n\n") {
                """
                |    vmObject["${it.propertyName}"] = vm.${it.propertyName}
                """.trimMargin()
            }

            val argConverter = if (vm.isNavigable) {
                """
                |fun ${vm.argsType?.type?.qualifiedName}.toParams() = json(
                |${
                    vm.argsType?.properties?.filter { it.isRoute }?.joinToString("\n") {
                        "|    \"${it.name}\" to argToParam(this.${it.name}),"
                    }
                }
                |)
                |
                |fun ${vm.argsType?.type?.qualifiedName}.toQuery() = json(
                |${
                    vm.argsType?.properties?.filter { !it.isRoute }?.joinToString("\n") {
                        "|    \"${it.name}\" to argToParam(this.${it.name}),"
                    }
                }
                |)
                |
                |fun jsonTo${vm.name.toUpperCamelCase()}Args(params: dynamic): ${vm.argsType?.type?.qualifiedName} = ${vm.argsType?.type?.qualifiedName}().also {
                |${
                    vm.argsType?.properties?.joinToString("\n") {
                        "it.${it.name} = paramTo${it.type?.simpleName}(params[\"${it.name}\"])"
                    }
                }
                |}
                |
                """.trimMargin()
            } else {
                ""
            }

            codeGenerator.createNewFile(
                dependencies,
                "com.latenighthack.viewmodel.vue",
                proxyClassName,
                "kt"
            ).apply {
                writeln(
                    """
                    |@file:Suppress("NON_EXPORTABLE_TYPE")
                    |
                    |package com.latenighthack.viewmodel.vue
                    |
                    |import com.latenighthack.viewmodel.common.fromHexString
                    |import com.latenighthack.viewmodel.common.*
                    |import com.latenighthack.viewmodel.*
                    |import com.latenighthack.viewmodel.common.*
                    |import kotlinx.browser.window
                    |import kotlinx.coroutines.*
                    |import kotlinx.coroutines.flow.Flow
                    |import kotlinx.coroutines.flow.collect
                    |import kotlin.js.Promise
                    |import kotlin.js.JsExport
                    |import kotlin.js.json
                    |
                    |$argConverter
                    |
                    |@OptIn(DelicateCoroutinesApi::class)
                    |@kotlin.js.ExperimentalJsExport
                    |@JsExport
                    |fun internal${proxyClassName}(props: dynamic, ref: (dynamic) -> dynamic, unmountCallback: (() -> Unit) -> Unit, bindingScope: BindingScope, vm: ${vmInterfaceName}): dynamic {
                    |    val vmObject = object {}.asDynamic()
                    |    val job = bindingScope.job
                    |    
                    |    vmObject.__job = bindingScope.job
                    |    vmObject.__bindingScope = bindingScope
                    |
                    |    // state
                    |${stateCopy("    ", "vmObject", "vm.initialState")}
                    |
                    |$actions
                    |
                    |$mutators
                    |
                    |$listsFwd
                    |
                    |$children
                    |
                    |$ignoredChildren
                    |
                    |    val vmObjectRef = ref(vmObject)
                    |
                    |    unmountCallback {
                    |        // do unbinding
                    |        job.cancel()
                    |    }
                    |
                    |    bindingScope.launch {
                    |        vm.state.collect { state ->
                    |${stateCopy("            ", "vmObjectRef", "state")}
                    |        }
                    |    }
                    |$lists
                    |
                    |    return vmObjectRef
                    |}
                    """.trimMargin()
                )
            }.close()
        }
    }
}
