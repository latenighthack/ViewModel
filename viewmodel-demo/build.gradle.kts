import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    alias(libs.plugins.binaryCompatibilityValidator)
    id("app.cash.licensee")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

kotlin {
    jvmToolchain(17)
    js {
        browser {
            commonWebpackConfig {
                this.outputFileName = "demo"
                this.output?.libraryTarget = "commonjs2"
            }
        }
        binaries.executable() // not applicable to BOTH, see details below
    }
    androidTarget {
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "library"
        }
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.viewmodelAnnotations)
                implementation(libs.kotlinx.coroutines.core)
                implementation(projects.viewmodelDemo.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jsMain by getting {
            dependencies {
            }
        }

        val iosMain by getting
    }
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
    namespace = "com.latenighthack.viewmodel.demo"
}
dependencies {
    implementation(project(":viewmodel-lib"))
}
