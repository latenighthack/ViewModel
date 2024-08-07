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
        all().forEach {
            it.builtins {
//                remove("java")
            }

            it.plugins {
                create("kt") {
                    outputSubDir = "kotlin"
                }
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
                implementation(libs.ktbuf)
                implementation(libs.coroutines.core)
            }
            kotlin {
                srcDirs(
                    "build/generated/source/proto/debug/kotlin",
                    "build/generated/source/proto/release/kotlin",
                )
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf)
                implementation(libs.coroutines.android)
            }
        }

        val iosX64Main by getting {
            dependencies {
                api(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf)
            }
        }
        val iosArm64Main by getting {
            dependencies {
                api(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependencies {
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf)
                api(libs.viewmodel.library)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.viewmodel.annotations)
                implementation(libs.viewmodel.library)
                implementation(libs.ktstore.library)
                implementation(libs.ktcrypto.library)
                implementation(libs.ktbuf)
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
        minSdk = 21
        targetSdk = 34
    }
    namespace = "com.latenighthack.viewmodel.demo.core"
}

dependencies {
    implementation(libs.viewmodel.library)
    implementation(libs.ktstore.library)
    implementation(libs.ktcrypto.library)
    implementation(libs.ktbuf)
}
