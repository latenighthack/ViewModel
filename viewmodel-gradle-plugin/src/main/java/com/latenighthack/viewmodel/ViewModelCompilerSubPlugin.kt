package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.ViewModelGradlePlugin.Companion.ARTIFACT_NAME
import com.latenighthack.viewmodel.ViewModelGradlePlugin.Companion.COMPILER_PLUGIN_ID
import com.latenighthack.viewmodel.ViewModelGradlePlugin.Companion.GROUP_NAME
import com.latenighthack.viewmodel.ViewModelGradlePlugin.Companion.MIN_KOTLIN_VERSION
import com.latenighthack.viewmodel.ViewModelGradlePlugin.Companion.SNAPSHOT
import com.latenighthack.viewmodel.ViewModelGradlePlugin.Companion.VIEWMODEL_VERSION
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

internal class ViewModelCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {
    private lateinit var myproject: Project
    private var gradleExtension: ViewModelGradleConfiguration = ViewModelGradleConfiguration()

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        gradleExtension = kotlinCompilation.target.project.getViewModelConfig()

        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption("enabled", "true"),
                SubpluginOption("logging", "false"),
            )
        }
    }

    override fun apply(target: Project) {
        myproject = target
    }

    override fun getCompilerPluginId(): String = COMPILER_PLUGIN_ID

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun getPluginArtifact(): SubpluginArtifact {
        checkKotlinVersion(myproject.kotlinExtension.compilerVersion.get())
        return SubpluginArtifact(
            groupId = GROUP_NAME,
            artifactId = ARTIFACT_NAME,
            version = "${VIEWMODEL_VERSION}-${myproject.kotlinExtension.compilerVersion.get()}$SNAPSHOT",
        )
    }

    private fun checkKotlinVersion(compilerVersion: String) {
        if (compilerVersion.split(".")[0] < MIN_KOTLIN_VERSION.split(".")[0]) {
            error("ViewModel: Kotlin version $compilerVersion is not supported. You need at least version $MIN_KOTLIN_VERSION")
        }
    }
}
