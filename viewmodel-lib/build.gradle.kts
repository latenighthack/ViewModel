plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("com.android.library")
    alias(libs.plugins.binaryCompatibilityValidator)
    id("app.cash.licensee")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {

    coordinates(
        libs.versions.groupId.get(),
        "viewmodel-lib",
        libs.versions.viewmodel.get(),
    )
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

kotlin {
    jvmToolchain(17)
    explicitApi()

    compilerOptions {
        // Common compiler options applied to all Kotlin source sets
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
//    if (libs.versions.ktorVersion
//            .get()
//            .startsWith("3.")
//    ) {
//        @OptIn(ExperimentalWasmDsl::class)
//        wasmJs()
//    }
    js(IR) {
        browser()
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
            baseName = "ViewModelCore"
        }
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.viewmodelAnnotations)
                implementation(libs.kotlinx.coroutines.core)
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
    namespace = "com.latenighthack.viewmodel"
}

publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])

            pom {
                name.set(project.name)
                description.set("ViewModel Client Library")
                url.set("https://github.com/latenighthack/viewmodel")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/latenighthack/viewmodel/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/latenighthack/viewmodel")
                    connection.set("scm:git:git://github.com/latenighthack/viewmodel.git")
                }
                developers {
                    developer {
                        name.set("Mike Roberts")
                        url.set("https://github.com/mproberts")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url =
                    when {
                        "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                        else -> property("sonatypeReleaseUrl")
                    } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}

multiplatformSwiftPackage {
    packageName("ViewModelCore")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
    outputDirectory(File(projectDir, "ViewModelCore"))
}
