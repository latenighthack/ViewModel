package com.latenighthack.viewmodel

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Locale.US

class ViewModelGradlePlugin : Plugin<Project> {
    companion object {
        const val GRADLE_TASKNAME = "viewmodel"
        const val GROUP_NAME = "com.latenighthack.viewmodel"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val COMPILER_PLUGIN_ID = "viewmodelPlugin"
        const val VIEWMODEL_VERSION = "0.0.3" // remember to bump this version before any release!
        const val SNAPSHOT = ""
        const val MIN_KSP_VERSION = "1.0.21"
        const val MIN_KOTLIN_VERSION = "2.0.0"
    }

    override fun apply(project: Project) {
        with(project) {
            extensions.create(GRADLE_TASKNAME, ViewModelGradleConfiguration::class.java)

            pluginManager.apply(ViewModelCompilerSubPlugin::class.java)

            val hasKspApplied = extensions.findByName("ksp") != null
            if (hasKspApplied) {
                val viewmodelKsp = "$GROUP_NAME:viewmodel-ksp"

                val kspPlugin =
                    plugins.findPlugin("com.google.devtools.ksp") ?: error("KSP plugin not found")

                val kspVersion =
                    kspPlugin.javaClass.protectionDomain.codeSource.location.toURI().toString()
                        .substringAfterLast("-").substringBefore(".jar")

                checkKSPVersion(kspVersion)

                val kspExtension = extensions.findByName("ksp") ?: error("KSP config not found")
                val argMethod = kspExtension.javaClass.getMethod("arg", String::class.java, String::class.java)

                afterEvaluate {
                    val config = getViewModelConfig()

                    argMethod.invoke(
                        kspExtension,
                        "ViewModel_NavigatorClassName",
                        config.navigatorClassName
                    )
                    argMethod.invoke(
                        kspExtension,
                        "ViewModel_ResolverClassName",
                        config.resolverClassName
                    )
                }

                val dependency = "$viewmodelKsp:$VIEWMODEL_VERSION-$kspVersion$SNAPSHOT"

                when (val kotlinExtension = kotlinExtension) {
                    is KotlinSingleTargetExtension<*> -> {
                        dependencies.add("ksp", dependency)
                    }

                    is KotlinMultiplatformExtension -> {
                        dependencies {
                            add("kspCommonMainMetadata", dependency)
                        }

                        kotlinExtension.targets.configureEach {
                            if (targetName == "metadata") return@configureEach
                            dependencies.add(
                                "ksp${
                                    targetName.replaceFirstChar {
                                        if (it.isLowerCase()) {
                                            it.titlecase(
                                                US,
                                            )
                                        } else {
                                            it.toString()
                                        }
                                    }
                                }",
                                dependency,
                            )

                            dependencies.add(
                                "ksp${
                                    targetName.replaceFirstChar {
                                        if (it.isLowerCase()) {
                                            it.titlecase(
                                                US,
                                            )
                                        } else {
                                            it.toString()
                                        }
                                    }
                                }Test",
                                dependency,
                            )
                        }

                        kotlinExtension.sourceSets.named("commonMain").configure {
                            kotlin.srcDir("${layout.buildDirectory.get()}/generated/ksp/metadata/commonMain/kotlin")
                        }

                        tasks.withType(KotlinCompilationTask::class.java).configureEach {
                            if (name != "kspCommonMainKotlinMetadata") {
                                dependsOn("kspCommonMainKotlinMetadata")
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun checkKSPVersion(kspVersion: String) {
        val kspVersionParts = kspVersion.split(".")
        if (kspVersionParts[2].toInt() < MIN_KSP_VERSION.split(".")[2].toInt()) {
            error("ViewModel: KSP version $kspVersion is not supported. You need at least version $MIN_KSP_VERSION")
        }
    }
}

internal fun Project.getViewModelConfig() = this.extensions.findByType(ViewModelGradleConfiguration::class.java) ?: ViewModelGradleConfiguration()
