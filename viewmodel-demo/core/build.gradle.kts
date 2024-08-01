buildscript {
    dependencies {
        classpath(libs.viewmodel.gradle.plugin)
    }
}

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
    id("com.android.library")
    id("com.latenighthack.viewmodel")
}

viewmodel {
    navigatorClassName = "com.latenighthack.viewmodel.core.Navigator"
    resolverClassName = "com.latenighthack.viewmodel.core.Core"
}

kotlin {
    jvmToolchain(17)
    js(IR) {
        browser {
        }
//        binaries.executable() // not applicable to BOTH, see details below
    }
    androidTarget {
        publishLibraryVariants("release", "debug")
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
            baseName = "core"
        }
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.viewmodelAnnotations)
                implementation(projects.viewmodelLib)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(projects.viewmodelLib)
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
    namespace = "com.latenighthack.viewmodel.demo.core"
}
dependencies {
    implementation(project(":viewmodel-lib"))
}
