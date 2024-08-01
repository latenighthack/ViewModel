plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("com.android.library")
    alias(libs.plugins.detekt)
    alias(libs.plugins.binaryCompatibilityValidator)
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    filter {
        exclude {
            it.file.path.contains(
                layout.buildDirectory
                    .dir("generated")
                    .get()
                    .toString(),
            )
        }
    }
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {

    coordinates(
        libs.versions.groupId.get(),
        "viewmodel-annotations",
        libs.versions.viewmodel.get(),
    )

    if (enableSigning) {
        signAllPublications()
    }
}

kotlin {
    jvmToolchain(17)
    explicitApi()
    js(IR) {
        browser()
//        binaries.executable()
    }
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    jvm()

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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by getting
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

java {
    toolchain {
        version = "1.7"
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
    namespace = "com.latenighthack.viewmodel.common"
}

publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])
            // artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("ViewModel c")
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
