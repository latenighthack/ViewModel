package com.latenighthack.viewmodel.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.latenighthack.viewmodel.codegen.v1.Type
import com.latenighthack.viewmodel.codegen.v1.ViewModelDeclaration

class VueScreenComponentGenerator(
    private val dependencies: Dependencies,
    private val codeGenerator: CodeGenerator,
    private val log: KSPLogger,
    private val options: Map<String, String>
) {
    val Type.argTypeName: String
        get() {
            return when (simpleName) {
                "ByteArray" -> "String"
                else -> simpleName
            }
        }

    fun generate(viewModels: List<ViewModelDeclaration>) {
        val allVms = viewModels
            .map {
                it.declaration!!.qualifiedName to it
            }
            .toMap()

        for (vm in viewModels) {
            val activityClassName = "${vm.name.toUpperCamelCase()}View"

            val lists = vm.lists.joinToString("\n\n") { list ->
                """
                |<template v-for="item in vm.${list.propertyName}">
                |${
                    if (list.allowableTypes.size == 1) {
                        "    <${allVms[list.allowableTypes.first().qualifiedName]!!.name.toUpperCamelCase()}ViewVue :model=\"item\" />"
                    } else {
                        list.allowableTypes.joinToString("\n") {
                            "    <${allVms[it.qualifiedName]!!.name.toUpperCamelCase()}ViewVue :model=\"item\" v-if=\"item.__type == ${it.qualifiedName.hashCode()}\" />"
                        }
                    }
                }
                |</template>
                """.trimMargin()
            }

            val allChildTypes = vm.children.map { "${allVms[it.type?.qualifiedName]!!.name.toUpperCamelCase()}" }
                .plus(vm.lists
                    .flatMap { it.allowableTypes }
                    .map { "${allVms[it.qualifiedName]!!.name.toUpperCamelCase()}" })

            val children = vm.children.joinToString("\n\n") { child ->
                val childVm = allVms[child.type?.qualifiedName]!!

                """
                |<${childVm.name.toUpperCamelCase()}ViewVue :model="vm.${child.propertyName}" />
                """.trimMargin()
            }

            val state = vm.state?.properties?.joinToString("\n\n") { property ->
                """
                |${
                    if (property.type?.simpleName == "ProfilePhotoModel") {
                        "<ProfilePhotoView :model=\"vm.${property.name}\" />"
                    } else {
                        "<p>{{ vm.${property.name} }}</p>"
                    }
                }
                """.trimMargin()
            } ?: ""

            // non-default actions
            val actions = vm.actions
                .filter { it.name?.noun != "item" }
                .joinToString("\n\n") { action ->
                    """
                    |<PrimaryButton class="enabled" @click="vm.${action.methodName}()">${action.name?.noun?.toUpperSpaced() ?: "???"}</PrimaryButton>
                    """.trimMargin()
                }

            val edits = vm.mutations.joinToString("\n\n") { edit ->
                """
                |<EditText placeholder="${edit.name?.noun?.toUpperSpaced() ?: ""}" :value="vm.${edit.name?.noun}" @input="event => vm.${edit.methodName}(event.target.value)" />
                """.trimMargin()
            }

            val vmDefinition = if (vm.isNavigable) {
                """
                |const props = defineProps({
                |    ${vm.argsType?.properties?.joinToString(", ") { "\"${it.name}\": ${it.type?.argTypeName}" }}
                |});
                |const vm = vueModels.${vm.name.toCamelCase()}ViewModel(Object.assign({}, props, query), core, navigator, shallowReactive, onUnmounted);
                """.trimMargin()
            } else {
                """
                |const props = defineProps(["model"]);
                |
                |const vm = props.model;
                """.trimMargin()
            }

            val defaultAction = vm.actions
                .firstOrNull { it.name?.noun == "item" && it.name.verb == "tapped" }?.let {
                    " @click=\"vm.${it.methodName}()\""
                } ?: ""

            codeGenerator.createNewFile(
                dependencies,
                "com.latenighthack.viewmodel.gen.components",
                activityClassName,
                "vue"
            ).apply {
                writeln(
                    """
                    |<template>
                    |   <div${defaultAction}>
                    |        ${vm.name.toUpperSpaced()}
                    |        ${children.split("\n").joinToString("\n        ")}
                    |        ${lists.split("\n").joinToString("\n        ")}
                    |        ${state.split("\n").joinToString("\n        ")}
                    |        ${edits.split("\n").joinToString("\n        ")}
                    |        ${actions.split("\n").joinToString("\n        ")}
                    |   </div>
                    |</template>
                    |
                    |<style scoped>
                    |</style>
                    |
                    |<script setup lang="js">
                    |import { routeLocationKey, useRoute } from "vue-router";
                    |import { vueModels } from "../../app-web";
                    |import { inject, shallowReactive, onUnmounted, defineProps } from "vue";
                    |${allChildTypes.joinToString("\n") { "import ${it}ViewVue from \"./${it}View.vue\";" }}
                    |
                    |const route = useRoute();
                    |const core = inject("core");
                    |const navigator = inject("navigator");
                    |const query = route.query;
                    |
                    |${vmDefinition}
                    |</script>
                    |
                    """.trimMargin()
                )
            }.close()
        }

        val componentImports = viewModels
            .filter { it.isNavigable }
            .joinToString("\n") {
                "import ${it.name.toUpperCamelCase()}ViewVue from \"../gen/components/${it.name.toUpperCamelCase()}View.vue\";"
            }

        fun toPath(vm: ViewModelDeclaration): String {
            return if (vm.webPath.isNullOrEmpty()) {
                "/${vm.name.toDashed()}" + vm
                    .argsType
                    ?.properties
                    ?.filter { it.isRoute }?.joinToString { "/:${it.name.toCamelCase()}" } ?: ""
            } else {
                vm.webPath
            }
        }

        val routes = viewModels
            .filter { it.isNavigable }
            .joinToString("\n") { vm ->
                "    { name: \"${vm.name.toCamelCase()}\", path: \"${toPath(vm)}\", components: { main: ${vm.name.toUpperCamelCase()}ViewVue }, props: true, " +
                        "params: {${
                            vm.argsType?.properties?.filter { it.isRoute }
                                ?.joinToString(", ") { "\"${it.name}\": ${it.type?.argTypeName}" }
                        }}, " +
                        "query: {${
                            vm.argsType?.properties?.filter { !it.isRoute }
                                ?.joinToString(", ") { "\"${it.name}\": ${it.type?.argTypeName}" }
                        }} },"
            }

        // generate routes
        codeGenerator.createNewFile(
            dependencies,
            "com.latenighthack.viewmodel.gen",
            "generatedRoutes",
            "js"
        ).apply {
            writeln(
                """
            |${componentImports}
            |
            |export const routes = [
            |${routes}
            |];
            """.trimMargin()
            )
        }.close()
    }

    fun allNouns(vm: ViewModelDeclaration) = vm.actions.mapNotNull { it.name?.noun }.toSet()
        .plus(vm.mutations.mapNotNull { it.name?.noun }.toSet())

    fun nonEditStateProperties(vm: ViewModelDeclaration) = vm.state!!.properties.filter { stateProperty ->
        allNouns(vm).firstOrNull {
            stateProperty.name.startsWith(it.toCamelCase())
        } == null
    }
}
