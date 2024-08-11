import com.google.devtools.ksp.gradle.KspTask
import com.google.protobuf.gradle.GenerateProtoTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ksp)
    alias(libs.plugins.viewmodel)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.20.1"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
//                remove("java")
            }

            task.plugins {
                create("kt") {
                    outputSubDir = "kotlin"
                }
            }

            task.outputs.upToDateWhen { false }

            val outputDir = task.outputBaseDir

            if (outputDir.indexOf("/proto/debug") > 0) {
                kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs("$outputDir/kotlin")
            }
        }
    }
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
    }
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    listOf(iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "Core"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.viewmodel.annotations)
                implementation(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf.library)
                implementation(libs.ktbuf.rpc)
                implementation(libs.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf.library)
                implementation(libs.ktbuf.rpc)
                implementation(libs.coroutines.android)
            }
        }

        val iosX64Main by getting {
            dependencies {
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf.library)
                implementation(libs.ktbuf.rpc)
                api(libs.viewmodel.library)
            }
        }
        val iosArm64Main by getting {
            dependencies {
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf.library)
                implementation(libs.ktbuf.rpc)
                api(libs.viewmodel.library)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependencies {
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf.library)
                implementation(libs.ktbuf.rpc)
                api(libs.viewmodel.library)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.viewmodel.annotations)
                implementation(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf.library)
                implementation(libs.ktbuf.rpc)
                implementation(libs.coroutines.core)
            }
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 26
        targetSdk = 34
    }
    namespace = "com.latenighthack.viewmodel.demo.core"
}

