import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        useCommonJs()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                outputPath = file("$projectDir/src/main/web/gen")
                devServer?.open = false
            }
            webpackTask {
                outputFileName = "app-web.js"
                output.libraryTarget = "commonjs2"
            }
            binaries.executable()
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            sourceMap = true
            sourceMapEmbedSources = JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_ALWAYS
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.viewmodel.library)
            implementation(libs.ktstore.library)
            implementation(projects.core)
            implementation(kotlin("stdlib-js"))
        }
        commonMain.dependencies {
            implementation(libs.viewmodel.library)
            implementation(libs.ktstore.library)
            implementation(projects.core)
        }
    }
}
